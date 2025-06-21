package elsys.amalino7.domain.model

import kotlinx.datetime.Instant
import java.util.*


data class Post(
    val id: UUID,
    val content: String,
    val imageId: UUID? = null,
    val imageUrl: String?,
    val user: User,
    val likeCount: Long,
    val hasLiked: Boolean,
    val commentCount: Long,
//    val repostCount: Long,
    val createdAt: Instant?,
    val updatedAt: Instant?
)
