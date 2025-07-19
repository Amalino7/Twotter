package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.comment.Comment
import elsys.amalino7.features.comment.CommentRepository
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class CommentRepositoryImpl : CommentRepository {
    override suspend fun getAll(input: PageRequest): PageResult<Comment> = query {
        val comments = Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
            .selectAll()
            .limit(100)
            .map { it.toComment() }
            .toList()
        return@query PageResult(comments, null)
    }

    override suspend fun create(
        model: Comment,
    ): Comment = query {
        val commentId = Comments.insertAndGetId {
            it[postId] = model.postId.toJavaUuid()
            it[userId] = model.user.id.toJavaUuid()
            it[content] = model.content
        }
        Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
            .selectAll()
            .where { Comments.id eq commentId }
            .single()
            .toComment()
    }

    override suspend fun getById(id: Long): Comment? = query {
        Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
            .selectAll()
            .where { Comments.id eq id.toInt() }
            .singleOrNull()
            ?.toComment()
    }

    override suspend fun delete(id: Long): Boolean = query {
        Comments.deleteWhere { (Comments.id eq id.toInt()) } > 0
    }

    override suspend fun getCommentsByPostId(postId: Uuid): List<Comment> = query {
        Comments.join(Users, JoinType.INNER, Comments.userId, Users.id)
            .selectAll()
            .where { Comments.postId eq postId.toJavaUuid() }
            .orderBy(Comments.timestamp, SortOrder.ASC)
            .map { it.toComment() }
            .toList()
    }

    override suspend fun update(model: Comment): Comment = query {
        Comments.update({ Comments.postId eq model.postId.toJavaUuid() }) {
            it[content] = model.content
        }
        model
    }

}