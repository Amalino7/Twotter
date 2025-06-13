package elsys.amalino7.domain.services

import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.repositories.PostRepository
import elsys.amalino7.domain.repositories.UserRepository
import elsys.amalino7.dto.PostCreateRequest
import kotlinx.datetime.Clock
import java.util.*

class PostService(
    val postRepo: PostRepository,
    val userRepo: UserRepository
) {
    suspend fun createPost(postCreateRequest: PostCreateRequest, user: User) {
        postRepo.addPost(
            Post(
                UUID.randomUUID(),
                postCreateRequest.content,
                imageUrl = postCreateRequest.imageUrl,
                user = user,
                likeCount = 0,
                commentCount = 0,
                repostCount = 0,
                hasLiked = false,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
            )
        )
    }

    suspend fun getPostById(id: String, requesterId: String? = null): Post? {
        return postRepo.getPostById(UUID.fromString(id), requesterId?.let { UUID.fromString(it) })
    }

    suspend fun deletePostById(id: String) = postRepo.deletePostById(UUID.fromString(id))
    suspend fun getAllPosts(requestId: String? = null): List<Post> {
        if (requestId == null) {
            return postRepo.getAllPosts(null)
        }
        return postRepo.getAllPosts(UUID.fromString(requestId))
    }

    suspend fun getPostsOfUser(userId: String, requesterId: String? = null): List<Post> {
        if (requesterId == null) {
            return postRepo.getPostsOfUser(UUID.fromString(userId))
        } else return postRepo.getPostsOfUser(UUID.fromString(userId), UUID.fromString(requesterId))
    }

    suspend fun getPostsOfUserByCriteria(userId: String) = postRepo.getPostsOfUserByCriteria(UUID.fromString(userId))
}