package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.Post
import java.util.*


interface PostRepository {
    suspend fun addPost(item: Post): Post
    suspend fun getPostById(id: UUID): Post?
    suspend fun updatePost(id: UUID, item: Post): Boolean
    suspend fun deletePostById(id: UUID): Boolean
    suspend fun getAllPosts(): List<Post>
//    suspend fun findBy(criteria: Map<String, Any>): List<Post>
}
