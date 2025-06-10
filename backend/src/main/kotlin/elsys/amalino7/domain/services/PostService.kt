package elsys.amalino7.domain.services

import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.repositories.PostRepository
import elsys.amalino7.domain.repositories.UserRepository
import elsys.amalino7.dto.PostCreateRequest
import kotlinx.datetime.Clock
import java.util.*

class PostService(
    val postRepo: PostRepository,
    val userRepo: UserRepository
) {
    suspend fun createPost(PostCreateRequest: PostCreateRequest) {
        val user = userRepo.getUserById(UUID.fromString(PostCreateRequest.userId))
        if (user == null) {
            throw Exception("User not found for post, id: ${PostCreateRequest.userId}")
        }

        postRepo.addPost(
            Post(
                UUID.randomUUID(),
                PostCreateRequest.content,
                imageUrl = PostCreateRequest.imageUrl,
                user = user,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
            )
        );
    }

    suspend fun getPostById(id: String) = postRepo.getPostById(UUID.fromString(id))
    suspend fun deletePostById(id: String) = postRepo.deletePostById(UUID.fromString(id))
    suspend fun getAllPosts() = postRepo.getAllPosts()
    suspend fun getPostsOfUser(userId: String) = postRepo.getPostsOfUser(UUID.fromString(userId))
    suspend fun getPostsOfUserByCriteria(userId: String) = postRepo.getPostsOfUserByCriteria(UUID.fromString(userId))
}