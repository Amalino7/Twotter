package elsys.amalino7.db

import Comments
import Users
import elsys.amalino7.domain.model.Comment
import elsys.amalino7.domain.repositories.CommentRepository
import elsys.amalino7.dto.CommentCreateRequest
import elsys.amalino7.dto.CommentUpdateRequest
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.*

class CommentRepositoryImpl : CommentRepository {

    /**
     * Adds a new comment to the database.
     * After insertion, it performs a join with the `Users` table to retrieve
     * the complete `User` object for the newly created comment, ensuring the
     * returned `Comment` object is fully populated.
     *
     * @param comment The data transfer object containing the comment details.
     * @return The newly created `Comment` object, including its associated user details.
     * @throws IllegalStateException if the comment cannot be retrieved after insertion,
     * which might indicate a database issue or a race condition.
     */
    override suspend fun addComment(comment: CommentCreateRequest): Comment {
        var newComment: Comment? = null
        transaction {
            val commentId = Comments.insertAndGetId {
                it[postId] = UUID.fromString(comment.postId)
                it[userId] = UUID.fromString(comment.userId)
                it[content] = comment.content
            }
            val resultRow = Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
                .selectAll()
                .where { Comments.id eq commentId }
                .singleOrNull()
            newComment = resultRow?.toComment()
        }
        return newComment
            ?: throw IllegalStateException("Failed to add comment: Could not retrieve the newly created comment.")
    }

    /**
     * Retrieves a single comment by its unique ID.
     * Performs an inner join with the `Users` table to include the associated user's details.
     *
     * @param commentId The integer ID of the comment to retrieve.
     * @return The `Comment` object if found, otherwise `null`.
     */
    override suspend fun getCommentById(commentId: Int): Comment? {
        return transaction {
            Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
                .selectAll()
                .where { Comments.id eq commentId }
                .singleOrNull()
                ?.toComment()
        }
    }

    /**
     * Retrieves all comments associated with a specific post ID.
     * Each comment returned will include the details of its author (user).
     * Comments are ordered by their creation timestamp in ascending order.
     *
     * @param postId The UUID of the post for which to retrieve comments.
     * @return A `List` of `Comment` objects. Returns an empty list if no comments are found for the post.
     */
    override suspend fun getCommentsByPostId(postId: UUID): List<Comment> {
        return transaction {
            Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
                .selectAll()
                .where { Comments.postId eq postId }
                .orderBy(Comments.timestamp, SortOrder.ASC)
                .map { it.toComment() }
        }
    }

    /**
     * Updates the content of an existing comment.
     *
     * @param commentId The ID of the comment to update.
     * @param request The data transfer object containing the new content for the comment.
     * @return `true` if the comment was successfully updated (i.e., one row was affected), `false` otherwise.
     */
    override suspend fun updateComment(request: CommentUpdateRequest): Boolean {
        return transaction {
            Comments.update({ Comments.id eq request.id }) {
                it[content] = request.content
            } > 0
        }
    }

    /**
     * Deletes a comment from the database by its ID.
     *
     * @param commentId The ID of the comment to delete.
     * @return `true` if the comment was successfully deleted (i.e., one row was affected), `false` otherwise.
     */
    override suspend fun deleteComment(commentId: Int): Boolean {
        return transaction {
            Comments.deleteWhere { Comments.id eq commentId } > 0
        }
    }
}