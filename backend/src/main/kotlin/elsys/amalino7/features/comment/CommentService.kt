package elsys.amalino7.features.comment

import elsys.amalino7.features.user.User
import elsys.amalino7.utils.AppException
import io.ktor.server.plugins.*
import kotlin.uuid.Uuid

class CommentService(val commentRepository: CommentRepository) {
    suspend fun create(dto: CommentCreateRequest, user: User, postId: String): CommentResponse? {
        return commentRepository.create(
            Comment(
                id = 0,
                postId = Uuid.parse(postId),
                content = dto.content,
                user = user
            )
        ).toResponse()
    }

    suspend fun get(id: Long, postId: String): CommentResponse? {
        val comment = commentRepository.getById(id)
        if (comment == null) {
            throw AppException.NotFoundException("Comment with id $id not found")
        }

        if (comment.postId != Uuid.parse(postId)) {
            throw AppException.ConflictException("Comment with id $id does not belong to post $postId")
        }

        return comment.toResponse()
    }

    suspend fun update(dto: CommentUpdateRequest, user: User, postId: String): CommentResponse {
        val oldComment = commentRepository.getById(dto.id.toLong())

        if (oldComment == null) {
            throw BadRequestException("Comment with id ${dto.id} not found")
        }

        if (oldComment.postId != Uuid.parse(postId)) {
            throw AppException.ConflictException("post id's don't match!")
        }
        if (oldComment.user.id != user.id) {
            throw AppException.UnauthorizedException("Comment with id ${dto.id} does not belong to user")
        }

        val newComment = Comment(
            id = oldComment.id,
            postId = oldComment.postId,
            content = dto.content ?: oldComment.content,
            user = user
        )
        return commentRepository.update(newComment).toResponse()
    }

    suspend fun delete(id: Long, postId: String, user: User): Boolean {
        val comment = commentRepository.getById(id)
        if (comment == null) {
            throw BadRequestException("Comment with id $id not found")
        }
        if (comment.postId != Uuid.parse(postId)) {
            throw AppException.ConflictException("post id's don't match!")
        }
        if (comment.user.id != user.id) {
            throw AppException.UnauthorizedException("Comment with id ${comment.id} does not belong to user")
        }
        return commentRepository.delete(id)
    }

    suspend fun getCommentsByPostId(postId: Uuid): List<CommentResponse> {
        return commentRepository.getCommentsByPostId(postId).map { it.toResponse() }
    }
}