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
        displayName = this[Users.displayName] ?: this[Users.username],
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

//fun ResultRow.toPost(hasLiked: ExpressionWithColumnTypeAlias<Boolean>): Post {
//    return Post(
//        id = this[Posts.id].value.toKotlinUuid(),
//        content = this[Posts.content],
//        user = this.toUser(),
//        createdAt = this[Posts.createdAt].toJavaInstant().toKotlinInstant(),
//        updatedAt = this[Posts.updatedAt].toJavaInstant().toKotlinInstant(),
//        likeCount = this.getOrNull(PostAggregates.likes) ?: 0,
//        commentCount = this.getOrNull(PostAggregates.comments) ?: 0,
//        imageId = this.getOrNull(Images.id)?.value?.toKotlinUuid(),
//        hasLiked = this.getOrNull(hasLiked) ?: false,
//        imageUrl = null
//    )
//}