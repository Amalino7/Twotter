package elsys.amalino7.domain.model

import kotlinx.datetime.Instant
import java.util.*

data class User(
    val id: UUID,
    val keycloakId: String,
    val name: String,
    val email: String,
    val bio: String? = null,
    val displayName: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
