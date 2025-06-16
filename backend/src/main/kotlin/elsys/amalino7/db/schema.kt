import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.countDistinct
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.timestamp

object Users : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val keycloakId = text("keycloak_id").uniqueIndex()
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
    val imageUrl = text("image_url").nullable()
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

object Likes : Table() {
    val userId = reference("user_id", Users).uniqueIndex()
    val postId = uuid("post_id").references(Posts.id).uniqueIndex()

    init {
        index(true, userId, postId) // Unique combo
    }
}

object Comments : IntIdTable("comments") {
    val postId = reference("post_id", Posts).index()
    val userId = reference("user_id", Users).index()
    val content = text("content")
    val timestamp = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

object Reposts : Table("reposts") {
    val userId = reference("user_id", Users).index()
    val postId = reference("post_id", Posts).index()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(userId, postId)
}


object PostAggregates {
    val likes = Likes.userId.countDistinct().alias("like_count")
    val comments = Comments.userId.countDistinct().alias("comment_count")
    val reposts = Reposts.userId.countDistinct().alias("repost_count")
}

//object hasLiked {
//    val hasLiked = Case()
//        .When((Likes.userId eq null) and (Likes.postId eq Posts.id), booleanLiteral(true))
//        .Else(booleanLiteral(false))
//        .alias("has_liked").alias("has_liked")
//}