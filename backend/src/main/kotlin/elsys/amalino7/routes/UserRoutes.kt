import elsys.amalino7.db.UserRepositoryImpl
import elsys.amalino7.domain.model.User
import elsys.amalino7.dto.UserDTO
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute() {
    get("/users") {
        val users = UserRepositoryImpl().getAllUsers().map {
            UserDTO(it.id,it.name, it.email)
        }
        call.respond(users)
    }
    get("/users/{id}") {
        val user = UserRepositoryImpl().getUserById(call.parameters["id"]!!)
        if (user == null) {
            call.respond(UserDTO("", "", ""))
            return@get
        }
        call.respond(UserDTO(user.id, user.name, user.email))
    }
    post("/users") {
        val user = call.receive<UserDTO>()
        val res = UserRepositoryImpl().addUser(User(user.id, user.name, user.email, "123456"))
        call.respond(UserDTO(res.id, res.name, res.email))
    }
}