package elsys.amalino7.db

import Comments
import PostAggregates
import Posts
import Users
import elsys.amalino7.domain.model.Comment
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.model.User
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.v1.core.ExpressionWithColumnTypeAlias
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.*


fun ResultRow.toUser(): User {
    return User(
        id = UUID.fromString(this[Users.id].toString()),
        name = this[Users.username],
        email = this[Users.email],
        bio = this[Users.bio],
        updatedAt = this[Users.updatedAt].toKotlinInstant(),
        createdAt = this[Users.createdAt].toKotlinInstant(),
        displayName = this[Users.displayName],
        keycloakId = this[Users.keycloakId]
    )
}

fun ResultRow.toPost(hasLiked: ExpressionWithColumnTypeAlias<Boolean>): Post {
    return Post(
        id = UUID.fromString(this[Posts.id].toString()),
        content = this[Posts.content],
        user = this.toUser(),
        createdAt = this[Posts.createdAt].toKotlinInstant(),
        updatedAt = this[Posts.updatedAt].toKotlinInstant(),
        likeCount = this.getOrNull(PostAggregates.likes) ?: 0,
        commentCount = this.getOrNull(PostAggregates.comments) ?: 0,
        repostCount = this.getOrNull(PostAggregates.reposts) ?: 0,
        hasLiked = this.getOrNull(hasLiked) ?: false,
    )
}

fun ResultRow.toComment(): Comment {
    return Comment(
        id = this[Comments.id].toString().toInt(),
        content = this[Comments.content],
        user = this.toUser(),
        postId = UUID.fromString(this[Comments.postId].toString()),
    )
}