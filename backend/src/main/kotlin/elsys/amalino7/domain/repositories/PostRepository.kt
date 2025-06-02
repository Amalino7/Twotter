package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.Post


interface PostRepository {
    suspend fun addPost(item: Post): Post
    suspend fun getPostById(id: String): Post?
    suspend fun updatePost(id: String, item: Post): Boolean
    suspend fun deletePostById(id: String): Boolean
    suspend fun getAllPosts(): List<Post>
//    suspend fun findBy(criteria: Map<String, Any>): List<Post>
}
