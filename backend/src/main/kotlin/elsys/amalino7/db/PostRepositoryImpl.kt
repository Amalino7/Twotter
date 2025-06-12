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
            Posts.insertReturning {
                it[id] = item.id
                it[content] = item.content
                it[imageUrl] = item.imageUrl
                it[user] = item.user.id
            }.single().toPost()
        }
    }


    private fun postQuery(userId: UUID? = null): Query {
        val hasLikedAlias = Case()
            .When((Likes.userId eq userId) and (Likes.postId eq Posts.id), booleanLiteral(true))
            .Else(booleanLiteral(false))
            .alias("has_liked")

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

    override suspend fun getPostById(id: UUID): Post? {
        return transaction {
            postQuery()
                .where { Posts.id eq id }
                .singleOrNull()
                ?.toPost()
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

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            postQuery()
                .limit(100)
                .map { it.toPost() }
        }
    }

    suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostsOfUser(userId: UUID): List<Post> {
        return transaction {
            postQuery().where { Posts.user eq userId }.map { it.toPost() }
        }
    }

    override suspend fun getPostsOfUserByCriteria(userId: UUID): List<Post> {
        return transaction {
            postQuery().adjustColumnSet {
                innerJoin(
                    Follows,
                    { Posts.user },
                    { Follows.followee }
                )
            }
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .where { Follows.follower eq userId }.map { it.toPost() }

        }
    }

}