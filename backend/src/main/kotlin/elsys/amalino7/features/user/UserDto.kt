package elsys.amalino7.features.user

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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
    var isFollowed: Boolean? = null
)

@Serializable
data class UserCreateRequest(
    val name: String,
    val email: String,
    val keycloakId: String
)

fun User.toResponse(isFollowed: Boolean?) = UserResponse(
    id.toString(), name, email, bio, displayName, createdAt, updatedAt, keycloakId, isFollowed
)

fun UserCreateRequest.toUser() = User(
    Uuid.random(), keycloakId, name, email, null, name, Clock.System.now(), Clock.System.now()
)

@Serializable
data class FollowerCreateRequest(val userId: String)

@Serializable
data class UserPatchRequest(
    val name: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val displayName: String? = null
)