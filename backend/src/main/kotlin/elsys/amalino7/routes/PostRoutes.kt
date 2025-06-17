package elsys.amalino7.routes

import com.auth0.jwt.interfaces.Payload
import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.services.PostService
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.PostCreateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

private suspend fun getUserId(principal: JWTPrincipal?): String? {
    if (principal == null) {
        return null
    }
    val subId = principal.payload.getClaim("sub").asString()
    val user =
        UserService().getUserByKeycloakId(subId) ?: return null
    return user.id.toString()
}

private suspend fun ApplicationCall.verifyUser(
    userId: String,
    jwtPayload: Payload
): User? {
    val userKeycloakId = jwtPayload.getClaim("sub").asString()
    val userEmail = jwtPayload.getClaim("email").asString()

    val user = UserService().getUserByKeycloakId(userKeycloakId)
    if (user == null) {
        this.respond(HttpStatusCode.Unauthorized, "User claim not found")
        return null
    }
    if (user.id.toString() != userId || user.email != userEmail) {
        this.respond(HttpStatusCode.Unauthorized, "User id not match")
        return null
    }
    return user
}

fun Route.postRoute(postService: PostService) {

    authenticate("auth-jwt", optional = true) {
        get("/posts") {
            val userId = getUserId(call.principal<JWTPrincipal>())
            val PostDTOs = postService.getAllPosts(userId).map { it.toResponse() }
            call.respond(PostDTOs)
        }
        get("/posts/{id}") {
            val userId = getUserId(call.principal<JWTPrincipal>())
            val id = call.parameters["id"]!!
            val response = postService.getPostById(id, requesterId = userId)?.toResponse()
            call.respond(response ?: HttpStatusCode.NotFound)
        }

        get("/users/{id}/posts") {
            val userId = call.parameters["id"]!!
            val posts = postService.getPostsOfUser(userId = userId, requesterId = getUserId(call.principal()))
            call.respond(posts.map { it.toResponse() })
        }
    }


    authenticate("auth-jwt") {
        post("/posts") {
            val post = call.receive<PostCreateRequest>()
            val user = call.verifyUser(post.userId, call.principal<JWTPrincipal>()!!.payload) ?: return@post
            val returnedPost = postService.createPost(post, user)
            return@post call.respond(returnedPost)
        }
        delete("/posts/{id}") {
            val id = call.parameters["id"]!!
            val principal = call.principal<JWTPrincipal>()!!.payload
            principal.getClaim("sub").asString()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "User claim not found")
            val post = postService.getPostById(id)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            if (post.user.keycloakId != principal.getClaim("sub").asString()) {
                call.respond(HttpStatusCode.Unauthorized, "User id not match")
                return@delete
            }

            val isDeleted = postService.deletePostById(id = id)
            if (isDeleted) {
                call.respond(HttpStatusCode.OK, "Post deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Post not found")
            }
        }
        get("/users/{id}/feed") {
            val userId = call.parameters["id"]!!
            val principal = call.principal<JWTPrincipal>()!!.payload
            val keyCloakID = principal.getClaim("sub").asString()
            val user = UserService().getUserByKeycloakId(keyCloakID)
            if (keyCloakID != userId || user == null) {
                return@get call.respond(HttpStatusCode.Unauthorized, "User id not match")
            }

            val posts = postService.getPostsOfUserByCriteria(userId = userId)
            call.respond(posts.map { it.toResponse() })
        }

        post("/posts/{id}/likes") {
            val id = call.parameters["id"]!!
            val principal = call.principal<JWTPrincipal>()!!.payload
            val keycloakID = principal.getClaim("sub").asString()
            val user = UserService().getUserByKeycloakId(keycloakID)
            if (keycloakID != user?.id.toString() || user == null) {
                return@post call.respond(HttpStatusCode.Unauthorized, "User id not match")
            }
            postService.postRepo.likePost(user.id, UUID.fromString(id))
        }
    }
}
