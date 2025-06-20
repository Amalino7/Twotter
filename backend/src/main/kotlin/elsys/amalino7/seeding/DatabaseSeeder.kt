import elsys.amalino7.seeding.Credential
import elsys.amalino7.seeding.KeycloakAdminClient
import elsys.amalino7.seeding.KeycloakConfig
import elsys.amalino7.seeding.KeycloakUser
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.*
import kotlin.random.Random

object DatabaseSeeder {

    fun seedProcedurally(keycloakConfig: KeycloakConfig, userCount: Int = 10, postsPerUser: Int = 5) {
        val keycloakAdminClient = KeycloakAdminClient(keycloakConfig)
        val faker = Faker()
        val contentGenerators = listOf<() -> String>(
            { faker.gameOfThrones().quote() },
            { faker.backToTheFuture().quote() },
            { faker.rickAndMorty().quote() },
            { faker.lebowski().quote() },
            { "A quote from Friends: ${faker.friends().quote()}" },
            { faker.chuckNorris().fact() },
            { "A thought on space: ${faker.space().star()}'s cluster is in the ${faker.space().galaxy()} galaxy." },
            { "From the Hitchhiker's Guide: ${faker.hitchhikersGuideToTheGalaxy().quote()}" },
            { faker.text().text(100, 200) }
        )

        transaction {
            // =================================================================
            // 1. Create Users in Keycloak and DB
            // =================================================================
            println("Seeding $userCount users...")
            val userIds = (1..userCount).mapNotNull { i ->
                val username = faker.name().username()
                val userToCreate = KeycloakUser(
                    username = username,
                    email = faker.internet().emailAddress(username),
                    emailVerified = true,
                    enabled = true,
                    credentials = listOf(Credential(value = "password123"))
                )

                val keycloakId = runBlocking { keycloakAdminClient.createUser(userToCreate) }

                if (keycloakId != null) {
                    runBlocking { keycloakAdminClient.updateUserAttributes(keycloakId, true, enabled = true) }
                    Users.insert {
                        it[this.username] = userToCreate.username
                        it[email] = userToCreate.email
                        it[this.keycloakId] = keycloakId
                        it[displayName] = username
                        it[bio] = faker.lorem().sentence(10, 5)
                        it[updatedAt] = Instant.now()
                    }[Users.id].value
                } else {
                    null
                }
            }
            println("Users seeded.")

            // =================================================================
            // 2. Create Posts for each User
            // =================================================================
            println("Seeding posts...")
            val postIds = mutableListOf<UUID>()
            userIds.forEach { userId ->
                (1..postsPerUser).forEach { i ->
                    val postId = Posts.insert {
                        it[user] = UUID.fromString(userId.toString())
                        it[content] = contentGenerators.random().invoke()
                        it[imageUrl] = faker.internet().image()
                        it[updatedAt] = Instant.now()
                    }[Posts.id].value
                    postIds.add(postId)
                }
            }
            println("Posts seeded.")

            // =================================================================
            // 3. Create a Social Graph (Follows, Likes, Reposts)
            // =================================================================
            println("Seeding social graph...")
            // Create Follows
            userIds.forEach { followerId ->
                val followees = userIds.shuffled().take(Random.nextInt(1, userIds.size / 2)).filter { it != followerId }
                Follows.batchInsert(followees, ignore = true) { followeeId ->
                    this[Follows.follower] = UUID.fromString(followerId.toString())
                    this[Follows.followee] = UUID.fromString(followeeId.toString())
                }
            }

            // Create Likes
            postIds.forEach { postId ->
                val likers = userIds.shuffled().take(Random.nextInt(0, userIds.size))
                Likes.batchInsert(likers, ignore = true) { likerId ->
                    this[Likes.userId] = UUID.fromString(likerId.toString())
                    this[Likes.postId] = postId
                }
            }

            // Create Reposts
            postIds.shuffled().take(postIds.size / 2).forEach { postId ->
                val reposter = userIds.random()
                // Simple check to avoid primary key collision, though DB would prevent it
                if (Reposts.selectAll()
                        .where { (Reposts.userId eq reposter) and (Reposts.postId eq postId) }
                        .count() == 0L
                ) {
                    Reposts.insert {
                        it[userId] = UUID.fromString(reposter.toString())
                        it[this.postId] = postId
                    }
                }
            }

            // Create Comments
            postIds.shuffled().take(postIds.size / 2).forEach { postId ->
                val commenters = userIds.shuffled().take(Random.nextInt(1, 4))
                commenters.forEach { commenterId ->
                    Comments.insert {
                        it[this.postId] = postId
                        it[userId] = UUID.fromString(commenterId.toString())
                        it[content] = faker.text().text(20, 100)
                    }
                }
            }
            println("Social graph seeded.")
            println("Seeding complete!")
        }
    }
}