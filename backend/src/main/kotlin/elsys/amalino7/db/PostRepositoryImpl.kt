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
                it[id] = item.id
                it[content] = item.content
                it[imageUrl] = item.imageUrl
                it[user] = item.userId
            }.single().toPost()
        }
    }


    override suspend fun getPostById(id: UUID): Post? {
        return transaction { Posts.selectAll().where { Posts.id eq id }.singleOrNull()?.toPost() }
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
            Posts.selectAll().map { it.toPost() }
        }
    }

    suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

}