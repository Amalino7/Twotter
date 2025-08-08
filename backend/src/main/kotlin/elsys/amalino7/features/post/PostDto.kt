package elsys.amalino7.features.post

import elsys.amalino7.utils.AppException
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
    val repostsCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val postType: PostType,
    val parentPost: ParentPostInfo? = null
)

@Serializable
data class ParentPostInfo(
    val id: String,
    val userHandle: String,
    val content: String
)

@Serializable
data class PostCreateRequest(
    val content: String,
    val imageId: String? = null,
    val parentPostId: String? = null, // For reply/quote functionality
    val postType: PostType = PostType.ORIGINAL
)

@Serializable
enum class PostType {
    ORIGINAL,
    REPLY,
    QUOTE
}

// Add validation extension
fun PostCreateRequest.validate() {
    when {
        content.isBlank() -> throw AppException.ValidationException("Content cannot be empty")
        content.length > 500 -> throw AppException.ValidationException("Content cannot exceed 500 characters")
        parentPostId != null && postType == PostType.ORIGINAL ->
            throw AppException.ValidationException("Original posts cannot have a parent post")
    }
}

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
        repostCount,
        createdAt,
        updatedAt,
        postType,
        parentPost?.let {
            ParentPostInfo(
                it.id.toString(),
                it.user.name,
                it.content
            )
        }
    )