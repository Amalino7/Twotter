import elsys.amalino7.db.UserRepositoryImpl
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.FollowerCreateRequest
import elsys.amalino7.dto.UserCreateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userService: UserService) {
    authenticate("auth-jwt") {
        get("/users") {
            val users = UserService().getAllUsers()
            call.respond(users)
        }
    }
    get("/users/{id}") {
        val userId = call.parameters["id"]!!
        val user = UserService().getUserById(userId)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(user)
    }
    post("/users/{id}/followers") {
        val userId = call.parameters["id"]!!
        val followerId = call.receive<FollowerCreateRequest>()
        UserRepositoryImpl().addFollowerForUser(
            userId = UUID.fromString(userId),
            followerId = UUID.fromString(followerId.userId)
        )

        call.respond(HttpStatusCode.OK)
    }
    get("/users/{id}/followers") {
        val userId = call.parameters["id"]!!
        val users = UserRepositoryImpl().getFollowersById(UUID.fromString(userId))
        call.respond(
            users.map { it.toResponse() }
        )
    }

    post("/users") {
        val userDto = call.receive<UserCreateRequest>()
        val user = UserService().addUser(userDto)
        call.respond(user)
    }
    delete("/users/{id}") {
        val userId = call.parameters["id"]!!
        val isDeleted = UserService().deleteUser(userId)
        if (isDeleted) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }

    }
}