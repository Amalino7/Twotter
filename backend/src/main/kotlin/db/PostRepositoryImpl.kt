package com.example.db

import com.example.db.dao.Post
import com.example.repositories.PostRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

class PostRepositoryImpl : PostRepository {
    override suspend fun create(item: Post): Post {
        Post.new { item }
        return Post.findById(item.id)!!
    }

    override suspend fun read(id: String): Post? {
        return Post.findById(id = UUID.fromString(id))
    }

    override suspend fun update(id: String, item: Post): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        return (Post.findById(UUID.fromString(id))?.delete() ?: false) as Boolean
    }

    override suspend fun getAll(): List<Post> {
        return transaction {
            Post.all().toList()
        }
    }

    override suspend fun findBy(criteria: Map<String, Any>): List<Post> {
        TODO("Not yet implemented")
    }

}