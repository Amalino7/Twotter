package elsys.amalino7.features.comment

import elsys.amalino7.features.user.User
import kotlin.uuid.Uuid

data class Comment(
    val id: Int,
    val postId: Uuid,
    val content: String,
    val user: User
)
