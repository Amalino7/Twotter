package elsys.amalino7.db

import Comments
import Follows
import Likes
import Posts
import Reposts
import Users
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.repositories.PostRepository
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class PostRepositoryImpl : PostRepository {
    override suspend fun addPost(item: Post): Post {
        return transaction {
            val postId = Posts.insertAndGetId {
                it[id] = item.id
                it[content] = item.content
                it[imageUrl] = item.imageUrl
                it[user] = item.user.id
            }
            return@transaction Posts
                .join(Users, JoinType.INNER, Comments.userId, Users.id)
                .selectAll()
                .where { Posts.id eq postId }
                .single().toPost(hasLikedAlias(item.user.id))
        }
    }

    private fun hasLikedCase(userId: UUID) = Case()
        .When((Likes.userId eq userId) and (Likes.postId eq Posts.id), booleanLiteral(true))
        .Else(booleanLiteral(false))
        .alias("has_liked")

    private fun hasLikedAlias(userId: UUID?) =
        if (userId != null) hasLikedCase(userId) else booleanLiteral(false).alias("has_liked")

    private fun postQuery(userId: UUID? = null): Query {
        val hasLikedAlias = hasLikedAlias(userId)

        return Posts
            .join(Users, JoinType.INNER, Posts.user, Users.id)
            .join(Likes, JoinType.LEFT, additionalConstraint = { Posts.id eq Likes.postId })
            .join(Comments, JoinType.LEFT, additionalConstraint = { Posts.id eq Comments.postId })
            .join(Reposts, JoinType.LEFT, additionalConstraint = { Posts.id eq Reposts.postId })
            .select(
                Posts.columns + Users.columns
                        + Likes.userId.countDistinct().alias("like_count")
                        + Comments.userId.countDistinct().alias("comment_count")
                        + Reposts.userId.countDistinct().alias("repost_count")
                        + hasLikedAlias
            )
            .groupBy(Posts.id, Users.id, Likes.userId, Likes.postId)
    }

    override suspend fun getPostById(id: UUID, requesterId: UUID?): Post? {
        return transaction {
            postQuery(requesterId)
                .where { Posts.id eq id }
                .singleOrNull()
                ?.toPost(hasLikedAlias(requesterId))
        }
    }

    override suspend fun updatePost(id: UUID, item: Post): Boolean {
        return transaction {
            Posts.update({ Posts.id eq id }) {
                it[content] = item.content
            } > 0
        }
    }

    override suspend fun deletePostById(id: UUID): Boolean {
        return transaction { Posts.deleteWhere { Posts.id eq id } > 0 }
    }

    override suspend fun getAllPosts(requesterId: UUID?): List<Post> {
        return transaction {
            postQuery()
                .limit(100)
                .map {
                    println(it)
                    it.toPost(hasLikedAlias(requesterId))
                }
        }
    }

    suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostsOfUser(userId: UUID, requesterId: UUID?): List<Post> {
        return transaction {
            postQuery(requesterId).where { Posts.user eq userId }.map { it.toPost(hasLikedAlias(requesterId)) }
        }
    }

    override suspend fun getPostsOfUserByCriteria(userId: UUID): List<Post> {
        return transaction {
            postQuery(userId).adjustColumnSet {
                innerJoin(
                    Follows,
                    { Posts.user },
                    { followee }
                )
            }
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .where { Follows.follower eq userId }.map { it.toPost(hasLikedAlias(userId)) }

        }
    }

}