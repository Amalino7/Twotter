package elsys.amalino7.routes

import elsys.amalino7.domain.repositories.CommentRepository
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.CommentCreateRequest
import elsys.amalino7.dto.CommentUpdateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

// --- Ktor Routing for Comments API ---
fun Route.commentsRoute(commentsService: CommentRepository) {
    // Protect all comment routes with the "auth-jwt" authentication provider
    authenticate("auth-jwt") {
        route("/comments") {
            // POST /comments - Add a new comment
            post {
                val principal = call.principal<JWTPrincipal>()
                val keycloakId = principal?.payload?.getClaim("keycloakId")?.asString()

                if (keycloakId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing Keycloak ID in token payload.")
                    return@post
                }

                val currentUser = UserService().getUserByKeycloakId(keycloakId)
                if (currentUser == null) {
                    call.respond(HttpStatusCode.Forbidden, "User associated with token not found in the system.")
                    return@post
                }

                // Receive the comment creation request (postId and content)
                val request = call.receive<CommentCreateRequest>()

                try {
                    // Add the comment using the extracted user ID
                    val newComment = commentsService.addComment(
                        request
                    )
                    call.respond(HttpStatusCode.Created, newComment.toResponse())
                } catch (e: Exception) {
                    application.log.error("Error adding comment: ${e.message}", e)
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add comment.")
                }
            }

            // GET /comments/{id} - Get a comment by its ID
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Comment ID must be a valid integer.")
                    return@get
                }

                val comment = commentsService.getCommentById(id)
                if (comment == null) {
                    call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
                } else {
                    call.respond(HttpStatusCode.OK, comment.toResponse())
                }
            }

            // GET /comments/post/{postId} - Get all comments for a specific post
            get("/post/{postId}") {
                val postIdString = call.parameters["postId"]
                val postId = try {
                    UUID.fromString(postIdString)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Post ID format.")
                    return@get
                }

                val comments = commentsService.getCommentsByPostId(postId)
                call.respond(HttpStatusCode.OK, comments.map { it.toResponse() })
            }

            // PUT /comments/{id} - Update an existing comment (only by its author)
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Comment ID must be a valid integer.")
                    return@put
                }

                val principal = call.principal<JWTPrincipal>()
                val keycloakId = principal?.payload?.getClaim("keycloakId")?.asString()

                if (keycloakId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing Keycloak ID in token payload.")
                    return@put
                }

                val currentUserId = UserService().getUserByKeycloakId(keycloakId)
                if (currentUserId == null) {
                    call.respond(HttpStatusCode.Forbidden, "User associated with token not found in the system.")
                    return@put
                }

                val request = call.receive<CommentUpdateRequest>()

                try {
                    // Pass the currentUserId for authorization check within the service
                    val updated = commentsService.updateComment(request)
                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Comment updated successfully.")
                    } else {
                        // This could mean comment not found or user is not the author
                        val comment = commentsService.getCommentById(id)
                        if (comment == null) {
                            call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
                        } else {
                            call.respond(HttpStatusCode.Forbidden, "You are not authorized to update this comment.")
                        }
                    }
                } catch (e: Exception) {
                    application.log.error("Error updating comment: ${e.message}", e)
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update comment.")
                }
            }

            // DELETE /comments/{id} - Delete a comment (only by its author)
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Comment ID must be a valid integer.")
                    return@delete
                }

                val principal = call.principal<JWTPrincipal>()
                val keycloakId = principal?.payload?.getClaim("keycloakId")?.asString()

                if (keycloakId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Missing Keycloak ID in token payload.")
                    return@delete
                }

                // TODO add check if user owns comment
                val currentUserId = UserService().getUserByKeycloakId(keycloakId)
                if (currentUserId == null) {
                    call.respond(HttpStatusCode.Forbidden, "User associated with token not found in the system.")
                    return@delete
                }

                try {
                    val deleted = commentsService.deleteComment(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        val comment = commentsService.getCommentById(id)
                        if (comment == null) {
                            call.respond(HttpStatusCode.NotFound, "Comment with ID $id not found.")
                        } else {
                            call.respond(HttpStatusCode.Forbidden, "You are not authorized to delete this comment.")
                        }
                    }
                } catch (e: Exception) {
                    application.log.error("Error deleting comment: ${e.message}", e)
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete comment.")
                }
            }
        }
    }
}