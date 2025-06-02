import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.UserCreateRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute() {
    get("/users") {
        val users = UserService().getAllUsers()
        call.respond(users)
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