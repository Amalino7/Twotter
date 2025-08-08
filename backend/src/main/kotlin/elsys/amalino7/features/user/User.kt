package elsys.amalino7.features.user

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class User(
    val id: Uuid,
    val keycloakId: String,
    val name: String,
    val email: String,
    val bio: String? = null,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

fun User.toView() = UserView(id, name, displayName)
data class UserView(
    val id: Uuid,
    val name: String,
    val displayName: String,
    val isFollowed: Boolean? = null
)