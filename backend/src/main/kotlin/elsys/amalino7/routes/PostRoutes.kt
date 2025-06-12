package elsys.amalino7.routes

import elsys.amalino7.domain.services.PostService
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.PostCreateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postRoute(postService: PostService) {
    get("/posts") {
        val PostDTOs = postService.getAllPosts().map { it.toResponse() }
        call.respond(PostDTOs)
    }
    get("/posts/{id}") {
        val id = call.parameters["id"]!!
        val response = postService.getPostById(id)?.toResponse()
        call.respond(response ?: HttpStatusCode.NotFound)
    }
    authenticate("auth-jwt") {
        post("/posts") {
            val post = call.receive<PostCreateRequest>()
            val principal = call.principal<JWTPrincipal>()!!.payload
            val keycloakId = principal.getClaim("sub").asString()
            val user = UserService().getUserByKeycloakId(keycloakId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "User claim not found")

            if (user.id.toString() != post.userId || user.email != principal.getClaim("email")
                    .asString()
            ) {
                return@post call.respond(HttpStatusCode.Unauthorized, "User id not match")
            }

            val returnedPost = postService.createPost(post, user)
            return@post call.respond(returnedPost)
        }
    }
    authenticate("auth-jwt") {
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

            val isDeleted = postService.deletePostById(id)
            if (isDeleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
    get("/users/{id}/posts") {
        val userId = call.parameters["id"]!!
        val posts = postService.getPostsOfUser(userId = userId)
        call.respond(posts.map { it.toResponse() })
    }
    authenticate("auth-jwt")
    {
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
    }
}
