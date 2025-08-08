package elsys.amalino7.infrastructure.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

object Users : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val keycloakId = text("keycloak_id").uniqueIndex()
    val profileImageId = reference("profile_image_id", Images.id).nullable()
    val email = text("email").uniqueIndex()
    val displayName = text("display_name").nullable()
    val bio = text("bio").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at")
//        .withDefinition("UPDATE", CurrentTimestamp)
        .defaultExpression(CurrentTimestamp)
}

object Posts : UUIDTable("posts") {
    val user = reference("user_id", Users)
    val content = text("content")
    val repostCount = long("repost_count").default(0)
    val postType = text("post_type").default("original")
    val imageId = reference("image_id", Images.id).nullable().uniqueIndex()
    val originalPost = reference("original_post_id", Posts).nullable().index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at")
//        .withDefinition("UPDATE", CurrentTimestamp)
        .defaultExpression(CurrentTimestamp)

}

object Follows : Table("follows") {
    val follower = reference("follower_id", Users)
    val followee = reference("followee_id", Users)
    val followedAt = timestamp("followed_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(follower, followee)
}

object Likes : CompositeIdTable("likes") {
    val userId = uuid("user_id").references(Users.id).entityId().index()
    val postId = uuid("post_id").references(Posts.id).entityId().index()

    override val primaryKey = PrimaryKey(userId, postId, name = "composite_id")
}

object Comments : IntIdTable("comments") {
    val postId = reference("post_id", Posts).index()
    val userId = reference("user_id", Users).index()
    val content = text("content")
    val parentCommentId = reference("parent_id", Comments).nullable()
    val timestamp = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

object Images : UUIDTable() {
    val uploaderId = reference("uploader_id", Users).nullable() // TODO fix
    val originalFileName = varchar("original_file_name", 255)
    val minioObjectKey = varchar("minio_object_key", 255).uniqueIndex()
    val contentType = varchar("content_type", 100)
    val uploadTimestamp = timestamp("upload_time").defaultExpression(CurrentTimestamp)
}

object PostAggregates {
    val likes = Likes.postId.count().alias("like_count")
    val comments = Comments.postId.count().alias("comment_count")
//    val reposts = Reposts.userId.countDistinct().alias("repost_count")
}

suspend fun <T> query(transaction: suspend () -> T): T {
    return suspendTransaction(Dispatchers.IO) {
        transaction()
    }
}