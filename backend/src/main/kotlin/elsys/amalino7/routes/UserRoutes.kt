import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.UserCreateRequest
import elsys.amalino7.dto.UserPatchRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userService: UserService) {
    get("/users") {
        val users = userService.getAllUsers()
        call.respond(users)
    }

    get("/users/{id}") {
        val userId = call.parameters["id"]!!
        val user = userService.getUserById(userId)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(user)
    }

    authenticate("auth-jwt") {
        post("/users") {
            val userDto = call.receive<UserCreateRequest>()
            val user = userService.addUser(userDto)
            call.respond(user)
        }

        patch("/users/{id}/") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid UUID format for ID")
                return@patch
            }

            val userPatch = try {
                call.receive<UserPatchRequest>()
            } catch (e: Exception) {
                call.application.log.error("Failed to parse UserPatchRequest: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, "Invalid request body for PATCH")
                return@patch
            }

            val patchedUser = userService.patchUser(id, userPatch)
            if (patchedUser != null) {
                call.respond(HttpStatusCode.OK, patchedUser)
            } else {
                call.respond(HttpStatusCode.NotFound, "User with ID $id not found or patch failed")
            }
        }
        delete("/users/{id}") {
            val userId = call.parameters["id"]!!
            val isDeleted = userService.deleteUser(userId)
            if (isDeleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }

        }
    }



    get("/users/{id}/followers") {
        val userId = call.parameters["id"]!!
        val users = userService.getFollowersById(UUID.fromString(userId))
        call.respond(
            users.map { it }
        )
    }

    get("/users/{id}/following") {
        val userId = call.parameters["id"]!!
        val users = userService.getFollowingById(UUID.fromString(userId))
        call.respond(users)
    }

    authenticate("auth-jwt") {
        post("/users/{id}/followers") {
            val userId = call.parameters["id"]!!
            val user = call.principal<User>()!!
            val followerId = user.id
            userService.addFollowerForUser(UUID.fromString(userId), followerId)
            call.respond(HttpStatusCode.OK)
        }
        delete("/users/{id}/followers") {
            val userId = call.parameters["id"]!!
            val user = call.principal<User>()!!
            val followerId = user.id
            userService.removeFollowerForUser(userId, followerId.toString())
        }
    }

}
