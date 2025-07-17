package elsys.amalino7.features.post

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: String,
    val content: String,
    val imageUrl: String? = null,
    val userId: String,
    val userHandle: String,
    val userDisplayName: String? = null,
    val hasLiked: Boolean,
    val likesCount: Long,
    val commentsCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class PostCreateRequest(
    val content: String,
    val imageId: String? = null,
)

@Serializable
data class PostPatchRequest(
    val content: String? = null,
)

fun Post.toResponse() =
    PostResponse(
        id.toString(),
        content,
        imageUrl,
        user.id.toString(),
        user.name,
        user.displayName,
        hasLiked,
        likeCount,
        commentCount,
        createdAt,
        updatedAt
    )