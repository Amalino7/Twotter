package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.Comment
import elsys.amalino7.dto.CommentCreateRequest
import elsys.amalino7.dto.CommentUpdateRequest
import java.util.*

interface CommentRepository {

    suspend fun addComment(comment: CommentCreateRequest): Comment
    suspend fun getCommentById(commentId: Int): Comment?

    //    suspend fun getAllComments(): List<Comment>
    suspend fun deleteComment(commentId: Int): Boolean
    suspend fun getCommentsByPostId(postId: UUID): List<Comment>
    suspend fun updateComment(request: CommentUpdateRequest): Boolean
}