package elsys.amalino7.dto

import elsys.amalino7.domain.model.Post
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: String,
    val content: String,
    val imageUrl: String? = null,
    val userHandle: String,
    val userDisplayName: String? = null,
    val hasLiked: Boolean,
    val likesCount: Long,
    val commentsCount: Long,
//    val repostsCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class PostCreateRequest(
    val content: String,
    val imageId: String? = null,
    val userId: String
)

fun Post.toResponse() =
    PostResponse(
        id.toString(),
        content,
        imageUrl,
        user.name,
        user.displayName,
        hasLiked,
        likeCount,
        commentCount,
//        repostCount,
        createdAt!!,
        updatedAt!!
    )

//suspend fun PostCreateRequest.toPost() =
//    Post(
//        UUID.randomUUID(),
//        content,
//        imageUrl,
//        user = UserRepositoryImpl().getUserById(UUID.fromString(userId))!!, /// TODO refactor
//        Clock.System.now(),
//        Clock.System.now()
//    )