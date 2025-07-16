package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.comment.Comment
import elsys.amalino7.features.user.User
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.uuid.toKotlinUuid


fun ResultRow.toUser(): User {
    return User(
        id = this[Users.id].value.toKotlinUuid(),
        name = this[Users.username],
        email = this[Users.email],
        bio = this[Users.bio],
        updatedAt = this[Users.updatedAt],
        createdAt = this[Users.createdAt],
        displayName = this[Users.displayName],
        keycloakId = this[Users.keycloakId]
    )
}

fun ResultRow.toComment(): Comment {
    return Comment(
        id = this[Comments.id].value,
        postId = this[Comments.postId].value.toKotlinUuid(),
        content = this[Comments.content],
        user = this.toUser(),
    )
}