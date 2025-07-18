package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.post.Post
import elsys.amalino7.features.post.PostRepository
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.r2dbc.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class PostRepositoryImpl : PostRepository {
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

    override suspend fun create(model: Post): Post {
        return query {
            Posts.insertAndGetId {
                it[id] = model.id.toJavaUuid()
                it[content] = model.content
                it[user] = model.user.id.toJavaUuid()
            }
            model.copy(id = model.id, hasLiked = false)
        }
    }

    private fun postQuery(userId: Uuid? = null): Query {
        val hasLikedAlias = hasLikedAlias(userId)
        return Posts
            .join(Images, JoinType.LEFT, Posts.imageId, Images.id)
            .join(Likes, JoinType.LEFT, additionalConstraint = { Posts.id eq Likes.postId })
            .join(Comments, JoinType.LEFT, additionalConstraint = { Posts.id eq Comments.postId })
            .join(Users, JoinType.INNER, Posts.user, Users.id)
            .select(
                Posts.columns + Images.minioObjectKey
                        + Users.columns
                        + Likes.postId.count().alias("like_count")
                        + Comments.postId.count().alias("comment_count")
                        + hasLikedAlias
            )
            .groupBy(Posts.id, Users.id, Images.minioObjectKey, Likes.userId)

    }

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

    override suspend fun delete(id: Uuid): Boolean = query {
        Posts.deleteWhere { Posts.id eq id.toJavaUuid() } > 0
    }
}