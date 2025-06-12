package elsys.amalino7.db

import Posts
import Users
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.model.User
import kotlinx.datetime.toKotlinInstant
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

fun ResultRow.toPost(): Post {
    return Post(
        id = UUID.fromString(this[Posts.id].toString()),
        content = this[Posts.content],
        user = this.toUser(),
        createdAt = this[Posts.createdAt].toKotlinInstant(),
        updatedAt = this[Posts.updatedAt].toKotlinInstant()
    )
}