package elsys.amalino7.dto

import elsys.amalino7.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val bio: String? = null,
    val displayName: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val keycloakId: String,
)

@Serializable
data class UserCreateRequest(
    val name: String,
    val email: String,
    val keycloakId: String
)

fun User.toResponse() = UserResponse(
    id.toString(), name, email, bio, displayName, createdAt, updatedAt, keycloakId
)

fun UserCreateRequest.toUser() = User(
    UUID.randomUUID(), keycloakId, name, email, null, null, Clock.System.now(), Clock.System.now()
)

@Serializable
data class FollowerCreateRequest(val userId: String)





