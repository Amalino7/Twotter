package elsys.amalino7.db

import Posts
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.repositories.PostRepository
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
                it[id] = UUID.randomUUID()
                it[content] = item.content
                it[user] = UUID.fromString(item.userID)
            }.single().toPost()
        }
    }


    override suspend fun getPostById(id: String): Post? {
        return transaction { Posts.selectAll().where { Posts.id eq UUID.fromString(id) }.singleOrNull()?.toPost() }
    }

    override suspend fun updatePost(id: String, item: Post): Boolean {
        return transaction {
            Posts.update({ Posts.id eq UUID.fromString(id) }) {
                it[content] = item.content
            } > 0
        }
    }

    override suspend fun deletePostById(id: String): Boolean {
        return transaction { Posts.deleteWhere { Posts.id eq UUID.fromString(id) } > 0 }
    }

    override suspend fun getAllPosts(): List<Post> {
        return transaction {
            Posts.selectAll().map { it.toPost() }
        }
    }

    suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

}