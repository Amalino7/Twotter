package elsys.amalino7.features.comment

import elsys.amalino7.features.user.User
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.Uuid

fun Route.CommentRoutes(commentService: CommentService) {
    route("/post/{postId}/comments") {
        get("/{id}") {
            val id = call.parameters["id"]!!
            val postId = call.parameters["postId"]!!
            val comment = commentService.get(id.toLong(), postId)
            if (comment == null) {
                call.respond(HttpStatusCode.NotFound, "comment with id $id not found")
            } else {
                call.respond(HttpStatusCode.OK, comment)
            }
        }
        get("/")
        {
            val postId = call.parameters["postId"]!!
            val comments = commentService.getCommentsByPostId(Uuid.parse(postId))
            call.respond(HttpStatusCode.OK, comments)
        }
        authenticate("auth-jwt") {
            post("/")
            {
                val postId = call.parameters["postId"]!!
                val commentDto = call.receive<CommentCreateRequest>()
                val user = call.principal<User>()!!
                val comment = commentService.create(commentDto, user, postId)
                if (comment == null) {
                    call.respond(HttpStatusCode.NotFound, "comment with id $postId not found")
                } else
                    call.respond(HttpStatusCode.OK, comment)
            }
            delete("/{id}") {
                val id = call.parameters["id"]!!
                val postId = call.parameters["postId"]!!
                val user = call.principal<User>()!!
                val success = commentService.delete(id.toLong(), postId, user)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Comment with id $id not found")
                }
            }
            patch("/{id}") {
                val id = call.parameters["id"]!!
                call.parameters["postId"]!!
                val newComment = call.receive<CommentUpdateRequest>()
                val user = call.principal<User>()!!
                commentService.update(newComment, user, id)

            }
        }

    }
}