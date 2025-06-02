package elsys.amalino7.domain.model

import kotlinx.datetime.Instant
import java.util.*


data class Post(
    val id: UUID,
    val content: String,
    val imageUrl: String? = null,
    val userId: UUID,
    val createdAt: Instant?,
    val updatedAt: Instant?
)
