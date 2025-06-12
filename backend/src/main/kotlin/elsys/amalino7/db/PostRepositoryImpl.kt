package elsys.amalino7.db

import Follows
import Posts
import Users
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.repositories.PostRepository
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
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


    override suspend fun getPostById(id: UUID): Post? {
        return transaction {
            Posts.join(Users, JoinType.LEFT, Posts.user, Users.id)
                .selectAll().where { Posts.id eq id }
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
            Posts.join(Users, JoinType.LEFT, Posts.user, Users.id)
                .selectAll()
                .map { it.toPost() }
        }
    }

    suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostsOfUser(userId: UUID): List<Post> {
        return transaction {
            Posts
                .join(Users, JoinType.LEFT, Posts.user, Users.id)
                .selectAll().where { Posts.user eq userId }.map { it.toPost() }
        }
    }

    override suspend fun getPostsOfUserByCriteria(userId: UUID): List<Post> {
        return transaction {
            Posts
                .join(Users, JoinType.LEFT, Posts.user, Users.id)
                .join(
                    Follows,
                    JoinType.INNER,
                    additionalConstraint = { Posts.user eq Follows.followee }
                ).selectAll()
                .orderBy(Posts.createdAt, SortOrder.DESC)
                .where { Follows.follower eq userId }.map { it.toPost() }
        }
    }

}