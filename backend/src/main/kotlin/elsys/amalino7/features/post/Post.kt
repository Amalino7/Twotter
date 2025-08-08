package elsys.amalino7.features.post

import elsys.amalino7.features.user.UserView
import kotlinx.datetime.Instant
import kotlin.uuid.Uuid

data class Post(
    val id: Uuid,
    val content: String,
    val imageId: Uuid? = null,
    val imageUrl: String?,
    val parentPost: Post? = null,
    val postType: PostType,
    val user: UserView,
    val likeCount: Long,
    val hasLiked: Boolean,
    val commentCount: Long,
    val repostCount: Long = 0,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    val isReply: Boolean get() = postType == PostType.REPLY
    val isQuote: Boolean get() = postType == PostType.QUOTE
    val isOriginal: Boolean get() = postType == PostType.ORIGINAL
}