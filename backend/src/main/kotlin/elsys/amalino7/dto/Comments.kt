package elsys.amalino7.dto

import elsys.amalino7.domain.model.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentCreateRequest(
    val postId: String,
    val userId: String,
    val content: String
)

/**
 * Data Transfer Object (DTO) for updating an existing comment.
 * Currently, only the content can be updated.
 */
@Serializable
data class CommentUpdateRequest(
    val id: Int,
    val content: String
)

@Serializable
data class CommentResponse(
    val id: Int,
    val postId: String,
    val content: String,
)

fun Comment.toResponse() = CommentResponse(id, postId.toString(), content)