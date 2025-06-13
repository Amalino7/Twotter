package elsys.amalino7.domain.model

import java.util.*

data class Comment(
    val id: Int,
    val postId: UUID,
    val content: String,
    val user: User
)
