package com.example.repositories

import com.example.db.dao.Post

interface PostRepository {
    suspend fun create(item: Post): Post
    suspend fun read(id: String): Post?
    suspend fun update(id: String, item: Post): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun getAll(): List<Post>
    suspend fun findBy(criteria: Map<String, Any>): List<Post>
}
