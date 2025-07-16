package elsys.amalino7.plugins


import elsys.amalino7.features.comment.CommentRoutes
import elsys.amalino7.features.user.userRoutes
import elsys.amalino7.utils.AppException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.getKoin

fun Application.configureRouting() {
//    install(RequestValidation) {
//        validate<String> { bodyText ->
//            if (!bodyText.startsWith("Hello"))
//                ValidationResult.Invalid("Body text should start with 'Hello'")
//            else ValidationResult.Valid
//        }
//    }
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.localizedMessage}")
        }
        exception<AppException.ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, "${cause.localizedMessage}")
        }
        exception<AppException.NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, "${cause.localizedMessage}")
        }
        exception<AppException.ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.localizedMessage}")
        }
        exception<AppException.UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, "${cause.localizedMessage}")
        }
        exception<AppException.DatabaseException> { call, cause ->
            if (cause.localizedMessage.contains("unique")) {
                call.respond(HttpStatusCode.BadRequest, "${cause.localizedMessage}")
            }
            cause.printStackTrace()
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<NumberFormatException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.localizedMessage}")
        }

        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        userRoutes(getKoin().get())
        CommentRoutes(getKoin().get())
    }
}
