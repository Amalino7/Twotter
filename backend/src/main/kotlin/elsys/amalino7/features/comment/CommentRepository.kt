package elsys.amalino7.features.comment

import elsys.amalino7.utils.CrudRepository
import kotlin.uuid.Uuid

interface CommentRepository : CrudRepository<Long, Comment> {
    suspend fun getCommentsByPostId(postId: Uuid): List<Comment>

}