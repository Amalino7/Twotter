package elsys.amalino7.features.post

import elsys.amalino7.features.user.User
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class Post(
    val id: Uuid,
    val content: String,
    val imageId: Uuid? = null,
    val imageUrl: String?,
    val parentPost: Post? = null,
    val user: User,
    val likeCount: Long,
    val hasLiked: Boolean,
    val commentCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant
)
