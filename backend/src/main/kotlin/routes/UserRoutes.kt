import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute() {
    get("/hello") {
        call.respondText("Hello World!");
    }
}