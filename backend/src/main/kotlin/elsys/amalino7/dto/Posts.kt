package elsys.amalino7.dto

import elsys.amalino7.domain.model.Post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PostResponse(
    val id: String,
    val content: String,
    val imageUrl: String? = null,
    val userId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class PostCreateRequest(
    val content: String,
    val imageUrl: String? = null,
    val userId: String
)

fun Post.toResponse() =
    PostResponse(id.toString(), content, imageUrl, userId.toString(), Clock.System.now(), Clock.System.now())

fun PostCreateRequest.toPost() = Post(UUID.randomUUID(), content, imageUrl, UUID.fromString(userId), null, null)