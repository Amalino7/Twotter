package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.Post
import java.util.*


interface PostRepository {
    suspend fun addPost(item: Post): Post
    suspend fun getPostById(id: UUID, requesterId: UUID? = null): Post?
    suspend fun updatePost(id: UUID, item: Post): Boolean
    suspend fun deletePostById(id: UUID): Boolean
    suspend fun getAllPosts(requesterId: UUID?): List<Post>
    suspend fun getPostsOfUser(userId: UUID, requesterId: UUID? = null): List<Post>
    suspend fun getPostsOfUserByCriteria(userId: UUID): List<Post>
//    suspend fun findBy(criteria: Map<String, Any>): List<Post>
}
