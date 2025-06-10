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
    PostResponse(id.toString(), content, imageUrl, user.name, user.displayName, createdAt!!, updatedAt!!)

//suspend fun PostCreateRequest.toPost() =
//    Post(
//        UUID.randomUUID(),
//        content,
//        imageUrl,
//        user = UserRepositoryImpl().getUserById(UUID.fromString(userId))!!, /// TODO refactor
//        Clock.System.now(),
//        Clock.System.now()
//    )