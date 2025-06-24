package elsys.amalino7.plugins

import elsys.amalino7.db.CommentRepositoryImpl
import elsys.amalino7.db.PostRepositoryImpl
import elsys.amalino7.db.UserRepositoryImpl
import elsys.amalino7.domain.services.PostService
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.routes.commentsRoute
import elsys.amalino7.routes.imageRoutes
import elsys.amalino7.routes.postRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import userRoute

fun Application.configureRouting() {
    val userService = UserService(userRepository = UserRepositoryImpl())
    val postService = PostService(PostRepositoryImpl(), UserRepositoryImpl())
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondText(text = "Invalid endpoint argument", status = HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: Server imploded :(((", status = HttpStatusCode.InternalServerError)
            println(cause.printStackTrace())
        }
    }

    routing {
        imageRoutes()
        userRoute(userService = userService)
        postRoute(postService = postService, userService = userService)
        commentsRoute(commentsService = CommentRepositoryImpl())
    }
}
