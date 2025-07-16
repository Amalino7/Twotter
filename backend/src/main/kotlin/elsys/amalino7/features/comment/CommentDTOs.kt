package elsys.amalino7.features.comment

import kotlinx.serialization.Serializable

@Serializable
data class CommentCreateRequest(
    val content: String
)

@Serializable
data class CommentUpdateRequest(
    val id: Int,
    val content: String?
)

@Serializable
data class CommentResponse(
    val id: Int,
    val postId: String,
    val content: String,
)

fun Comment.toResponse() = CommentResponse(id, postId.toString(), content)