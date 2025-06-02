package elsys.amalino7.db

import Users
import elsys.amalino7.domain.model.Post
import elsys.amalino7.domain.model.User
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toUser(): User {
    return User(
        id = this[Users.id].toString(),
        name = this[Users.username],
        email = this[Users.email],
        password = this[Users.passwordHash],
    )
}

fun ResultRow.toPost(): Post {
    return Post(
        content = this[Posts.content],
        userID = this[Posts.user].toString()
    )
}