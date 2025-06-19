package elsys.amalino7.routes

import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.repositories.CommentRepository
import elsys.amalino7.dto.CommentCreateRequest
import elsys.amalino7.dto.CommentUpdateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/**
 * Defines the routes for comment-related operations.
 * This structure groups all endpoints under `/comments` and applies authentication selectively.
 */
fun Route.commentsRoute(commentsService: CommentRepository) {

    route("/comments") {
        // --- PUBLIC ENDPOINTS --- //

        // GET /comments/{id} - Get a single comment by its ID
        get("/{id}") {
            val id = call.getValidIntParameter("id") ?: return@get
            val comment = commentsService.getCommentById(id)
            if (comment != null) {
                call.respond(HttpStatusCode.OK, comment.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
            }
        }

        // GET /comments/post/{postId} - Get all comments for a specific post
        get("/post/{postId}") {
            val postId = call.getValidUuidParameter("postId") ?: return@get
            val comments = commentsService.getCommentsByPostId(postId)
            call.respond(HttpStatusCode.OK, comments.map { it.toResponse() })
        }


        // --- AUTHENTICATED ENDPOINTS --- //
        authenticate("auth-jwt") {

            // POST /comments - Add a new comment
            post {
                val user = call.principal<User>()!! // Principal is guaranteed non-null here
                val request = call.receive<CommentCreateRequest>()

                // It's good practice to ensure the user creating the comment is the one in the request
                val commentWithAuthor = request.copy(userId = user.id.toString())

                val newComment = commentsService.addComment(commentWithAuthor)
                call.respond(HttpStatusCode.Created, newComment.toResponse())
            }

            // PUT /comments/{id} - Update an existing comment
            put("/{id}") {
                val id = call.getValidIntParameter("id") ?: return@put
                val user = call.principal<User>()!!
                val request = call.receive<CommentUpdateRequest>()

                val existingComment = commentsService.getCommentById(id)

                when {
                    existingComment == null -> {
                        call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
                    }

                    existingComment.user.id != user.id -> {
                        call.respond(HttpStatusCode.Forbidden, "You are not authorized to update this comment.")
                    }

                    else -> {
                        commentsService.updateComment(request)
                        call.respond(HttpStatusCode.OK, "Comment updated successfully.")
                    }
                }
            }

            // DELETE /comments/{id} - Delete a comment
            delete("/{id}") {
                val id = call.getValidIntParameter("id") ?: return@delete
                val user = call.principal<User>()!!

                val existingComment = commentsService.getCommentById(id)

                when {
                    existingComment == null -> {
                        call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
                    }

                    existingComment.user.id != user.id -> {
                        call.respond(HttpStatusCode.Forbidden, "You are not authorized to delete this comment.")
                    }

                    else -> {
                        commentsService.deleteComment(id)
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}


// --- HELPER FUNCTIONS --- //

/**
 * Extension function to safely parse an Integer parameter.
 * Responds with BadRequest if the parameter is missing or invalid.
 */
private suspend fun ApplicationCall.getValidIntParameter(name: String): Int? {
    val paramValue = parameters[name]
    if (paramValue == null) {
        respond(HttpStatusCode.BadRequest, "Missing '$name' parameter.")
        return null
    }
    return paramValue.toIntOrNull().also {
        if (it == null) {
            respond(HttpStatusCode.BadRequest, "'$name' must be a valid integer.")
        }
    }
}

/**
 * Extension function to safely parse a UUID parameter.
 * Responds with BadRequest if the parameter is missing or invalid.
 */
private suspend fun ApplicationCall.getValidUuidParameter(name: String): UUID? {
    val paramValue = parameters[name]
    if (paramValue == null) {
        respond(HttpStatusCode.BadRequest, "Missing '$name' parameter.")
        return null
    }
    return try {
        UUID.fromString(paramValue)
    } catch (_: IllegalArgumentException) {
        respond(HttpStatusCode.BadRequest, "Invalid '$name' format. Must be a UUID.")
        null
    }
}