package elsys.amalino7.features.user

import elsys.amalino7.utils.AppException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

fun Route.userRoutes(
    userService: UserService,
) {
    get("/users") {
        val users = userService
            .getAll()
            .map { it.toResponse() }
        call.respond(users)
    }
    get("/users/{id}") {
        val userId = call.parameters["id"]!!
        val user = userService.getById(Uuid.parse(userId))
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }
    post("/users") {
        val user = call.receive<UserCreateRequest>()
        userService.create(user.toUser())
        call.respond(user)
    }
    patch("/users/{id}") {
        val userId = call.parameters["id"]!!
        val newUser = call.receive<UserPatchRequest>()
        val oldUser = userService.getById(Uuid.parse(userId))
        if (oldUser == null) {
            throw AppException.NotFoundException("User not found")
        }
        val finalUser = userService.update(
            User(
                id = oldUser.id,
                keycloakId = oldUser.keycloakId,
                name = newUser.name ?: oldUser.name,
                email = newUser.email ?: oldUser.email,
                bio = newUser.bio ?: oldUser.bio,
                displayName = newUser.displayName ?: oldUser.displayName,
                createdAt = oldUser.createdAt,
                updatedAt = Clock.System.now(),
            )
        )
        call.respond(HttpStatusCode.OK, finalUser)
    }
    delete("/users/{id}") {
        val userId = call.parameters["id"]!!
        val res = userService.delete(Uuid.parse(userId))
        if (!res) {
            call.respond(HttpStatusCode.NotFound)
        }
        call.respond(HttpStatusCode.NoContent)
    }
    authenticate("auth-jwt") {
        get("/users/me") {
            val user = call.principal<User>()!!
            call.respond(user)
        }
    }

}
