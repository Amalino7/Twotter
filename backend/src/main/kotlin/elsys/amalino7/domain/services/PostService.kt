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
                imageId = postCreateRequest.imageId?.let { UUID.fromString(it) },
                imageUrl = null,
                user = user,
                likeCount = 0,
                commentCount = 0,
//                repostCount = 0,
                hasLiked = false,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
            )
        )
    }

    suspend fun getPostById(id: String, requesterId: UUID? = null): Post? {
        return postRepo.getPostById(UUID.fromString(id), requesterId)
    }

    suspend fun deletePostById(id: String) = postRepo.deletePostById(UUID.fromString(id))
    suspend fun getAllPosts(requestId: UUID? = null): List<Post> {
        return postRepo.getAllPosts(requestId)
    }

    suspend fun getPostsOfUser(userId: String, requesterId: UUID? = null): List<Post> {
        if (requesterId == null) {
            return postRepo.getPostsOfUser(UUID.fromString(userId))
        } else return postRepo.getPostsOfUser(UUID.fromString(userId), requesterId)
    }

    suspend fun getPostsOfUserByCriteria(userId: String) = postRepo.getPostsOfUserByCriteria(UUID.fromString(userId))
}