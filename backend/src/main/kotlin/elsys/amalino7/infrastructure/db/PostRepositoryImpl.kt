package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.post.Post
import elsys.amalino7.features.post.PostRepository
import elsys.amalino7.features.post.PostType
import elsys.amalino7.features.user.UserView
import elsys.amalino7.features.user.toView
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import io.ktor.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.r2dbc.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class PostRepositoryImpl : PostRepository {
    private fun postQuery(userId: Uuid? = null, includeParent: Boolean = false): Query {
        val hasLikedAlias = hasLikedAlias(userId)

        var query = Posts
            .join(Images, JoinType.LEFT, Posts.imageId, Images.id)
            .join(Likes, JoinType.LEFT, additionalConstraint = { Posts.id eq Likes.postId })
            .join(Comments, JoinType.LEFT, additionalConstraint = { Posts.id eq Comments.postId })
            .join(Users, JoinType.INNER, Posts.user, Users.id)

        // Join with parent post if needed
        if (includeParent) {
            query = query.join(
                Posts.alias("parent_posts"),
                JoinType.LEFT,
                additionalConstraint = { Posts.originalPost eq Posts.alias("parent_posts")[Posts.id] }
            )
                .join(
                    Users.alias("parent_users"),
                    JoinType.LEFT,
                    additionalConstraint = {
                        Posts.alias("parent_posts")[Posts.id] eq Users.alias("parent_users")[Users.id]
                    }
                )
        }

        return query.select(
            Posts.columns +
                    Images.minioObjectKey +
                    Users.columns +
                    Likes.postId.count().alias("like_count") +
                    Comments.postId.count().alias("comment_count") +
                    hasLikedAlias +
                    (if (includeParent) {
                        listOf(
                            Posts.alias("parent_posts")[Posts.id],
                            Posts.alias("parent_posts")[Posts.content],
                            Users.alias("parent_users")[Users.username],
                            Users.alias("parent_users")[Users.displayName]
                        )
                    } else emptyList())
        ).groupBy(
            Posts.id,
            Users.id,
            Images.minioObjectKey,
            Likes.userId,
            *(if (includeParent) arrayOf(
                Posts.alias("parent_posts")[Posts.id],
                Posts.alias("parent_posts")[Posts.content],
                Users.alias("parent_users")[Users.username],
                Users.alias("parent_users")[Users.displayName]
            ) else emptyArray())
        )
    }

    // TODO fix and add
    suspend fun getPostThread(postId: Uuid): List<Post> = query {
        postQuery(includeParent = true)
            .where {
                (Posts.id eq postId.toJavaUuid()) or
                        (Posts.originalPost eq postId.toJavaUuid())
            }
            .orderBy(Posts.createdAt, SortOrder.ASC)
            .map { it.toPost(hasLikedAlias(null)) }
            .toList()
    }

    override suspend fun create(model: Post): Post {
        return query {
            Posts.insertAndGetId {
                it[id] = model.id.toJavaUuid()
                it[content] = model.content
                it[user] = model.user.id.toJavaUuid()
                it[imageId] = model.imageId?.toJavaUuid()
                it[postType] = model.postType.toString().toLowerCasePreservingASCIIRules()
                it[originalPost] = model.parentPost?.id?.toJavaUuid()
                it[createdAt] = model.createdAt
                it[updatedAt] = model.updatedAt
            }

            // Increment repost count for parent post if this is a quote/repost
            if (model.parentPost != null && model.postType == PostType.QUOTE) {
                Posts.update({ Posts.id eq model.parentPost.id.toJavaUuid() }) {
                    with(SqlExpressionBuilder) {
                        it.update(repostCount, repostCount + 1)
                    }
                }
            }

            model.copy(id = model.id, hasLiked = false)
        }
    }

    override suspend fun getPostsByType(userId: Uuid, type: PostType): List<Post> = query {
        postQuery(userId, includeParent = true)
            .where {
                (Posts.user eq userId.toJavaUuid()) and
                        (Posts.postType eq type.toString())
            }
            .orderBy(Posts.createdAt, SortOrder.DESC)
            .map { it.toPost(hasLikedAlias(userId)) }
            .toList()
    }

    override suspend fun delete(id: Uuid): Boolean = query {
        val post = postQuery(includeParent = true)
            .where { Posts.id eq id.toJavaUuid() }
            .singleOrNull()
            ?.toPost(hasLikedAlias(null))

        if (post?.parentPost != null && post.postType == PostType.QUOTE) {
            Posts.update({ Posts.id eq post.parentPost.id.toJavaUuid() }) {
                with(SqlExpressionBuilder) {
                    it.update(repostCount, repostCount - 1)
                }
            }
        }

        Posts.deleteWhere { Posts.id eq id.toJavaUuid() } > 0
    }

    // Update the mapper function to handle parent post information
    private fun ResultRow.toPost(hasLiked: ExpressionWithColumnTypeAlias<Boolean>): Post {
        val parentPostId = this.getOrNull(Posts.alias("parent_posts")[Posts.id])?.value?.toKotlinUuid()
        val parentPost = if (parentPostId != null) {
            Post(
                id = parentPostId,
                content = this[Posts.alias("parent_posts")[Posts.content]],
                imageId = null,
                imageUrl = null,
                parentPost = null,
                postType = PostType.ORIGINAL,
                user = UserView(
                    id = this[Users.alias("parent_users")[Users.id]].value.toKotlinUuid(),
                    name = this[Users.alias("parent_users")[Users.username]],
                    displayName = this.getOrNull(Users.alias("parent_users")[Users.displayName])
                        ?: this[Users.alias("parent_users")[Users.username]],
                ),
                likeCount = 0,
                hasLiked = false,
                commentCount = 0,
                repostCount = 0,
                createdAt = this[Posts.createdAt],
                updatedAt = this[Posts.updatedAt]
            )
        } else null

        return Post(
            id = this[Posts.id].value.toKotlinUuid(),
            content = this[Posts.content],
            user = this.toUser().toView(),
            createdAt = this[Posts.createdAt],
            updatedAt = this[Posts.updatedAt],
            likeCount = this.getOrNull(PostAggregates.likes) ?: 0,
            commentCount = this.getOrNull(PostAggregates.comments) ?: 0,
            repostCount = this.getOrNull(Posts.repostCount) ?: 0,
            imageId = this.getOrNull(Images.id)?.value?.toKotlinUuid(),
            hasLiked = this.getOrNull(hasLiked) ?: false,
            imageUrl = this.getOrNull(Images.minioObjectKey)?.let { "/$it" },
            postType = this[Posts.postType].let { PostType.valueOf(it.toUpperCasePreservingASCIIRules()) },
            parentPost = parentPost
        )
    }

    override suspend fun getAll(input: PageRequest): PageResult<Post> {
        return query {
            val items = postQuery()
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .offset(input.size * (input.page - 1))
                .limit(input.size)
                .map {
                    it.toPost(hasLikedAlias(null))
                }
                .toList()
            PageResult(items, null)
        }
    }

    private fun hasLikedCase(userId: Uuid) = Case()
        .When(Likes.userId eq userId.toJavaUuid(), Op.TRUE)
        .Else(Op.FALSE)
        .alias("has_liked")

    private fun hasLikedAlias(userId: Uuid?) =
        if (userId != null) hasLikedCase(userId) else booleanLiteral(false).alias("has_liked")

    override suspend fun getById(id: Uuid): Post? {
        return query {
            postQuery(null)
                .where { Posts.id eq id.toJavaUuid() }
                .singleOrNull()
                ?.toPost(hasLikedAlias(null))
        }
    }

    override suspend fun update(model: Post): Post = query {
        Posts.update({ Posts.id eq model.id.toJavaUuid() }) {
            it[content] = model.content
            it[updatedAt] = model.updatedAt
        }
        model
    }

    override suspend fun getPostsOfUser(userId: Uuid, requesterId: Uuid?): List<Post> {
        return query {
            postQuery(requesterId)
                .where { Posts.user eq userId.toJavaUuid() }
                .map { it.toPost(hasLikedAlias(requesterId)) }
                .toList()
        }
    }

    override suspend fun getPostsOfUserByCriteria(userId: Uuid): List<Post> {
        return query {
            postQuery(userId).adjustColumnSet {
                innerJoin(
                    Follows,
                    { Posts.user },
                    { followee }
                )
            }
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .where { Follows.follower eq userId.toJavaUuid() }.map { it.toPost(hasLikedAlias(userId)) }
                .toList()

        }
    }

    override suspend fun likePost(postId: Uuid, userId: Uuid) = query {
        Likes.insertIgnore {
            it[Likes.postId] = postId.toJavaUuid()
            it[Likes.userId] = userId.toJavaUuid()
        }
        Unit
    }

    override suspend fun unlikePost(postId: Uuid, userId: Uuid) = query {
        Likes.deleteWhere { (Likes.postId eq postId.toJavaUuid()) and (Likes.userId eq userId.toJavaUuid()) }
        Unit
    }
}