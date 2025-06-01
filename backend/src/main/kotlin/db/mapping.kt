import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.timestamp

object Users : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val email = text("email").uniqueIndex()
    val passwordHash = text("password_hash")
    val displayName = text("display_name").nullable()
    val bio = text("bio").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
        .withDefinition("ON UPDATE", CurrentTimestamp)
}

object Posts : UUIDTable("posts") {
    val user = reference("user_id", Users)
    val content = text("content")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
        .withDefinition("ON UPDATE", CurrentTimestamp)
}

object Follows : Table("follows") {
    val follower = reference("follower_id", Users)
    val followee = reference("followee_id", Users)
    val followedAt = timestamp("followed_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(follower, followee)
}

//// AUTH TOKENS
//object AuthTokens : Table("auth_tokens") {
//    val userId = reference("user_id", Users)
//    val token = varchar("token", 512).primaryKey()
//    val createdAt = timestamp("created_at")
//    val expiresAt = timestamp("expires_at").nullable()
//}