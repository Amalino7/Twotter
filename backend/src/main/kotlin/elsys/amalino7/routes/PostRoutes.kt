package elsys.amalino7.routes

import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.services.PostService
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.PostCreateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.postRoute(postService: PostService, userService: UserService) {

    authenticate("auth-jwt", optional = true) {
        get("/posts") {
            val user = call.principal<User>()
            val PostDTOs = postService.getAllPosts(user?.id).map { it.toResponse() }
            call.respond(PostDTOs)
        }
        get("/posts/{id}") {
            val userId = call.principal<User>()?.id
            val id = call.parameters["id"]!!
            val response = postService.getPostById(id, requesterId = userId)?.toResponse()
            call.respond(response ?: HttpStatusCode.NotFound)
        }

        get("/users/{id}/posts") {
            val userId = call.parameters["id"]!!
            val posts =
                postService.getPostsOfUser(userId = userId, requesterId = call.principal<User>()?.id)
            call.respond(posts.map { it.toResponse() })
        }
    }


    authenticate("auth-jwt") {
        post("/posts") {
            val post = call.receive<PostCreateRequest>()
            val user = call.principal<User>()!!
            val returnedPost = postService.createPost(post, user)
            return@post call.respond(HttpStatusCode.Created, returnedPost)
        }

        delete("/posts/{id}") {
            val id = call.parameters["id"]!!
            val userId = call.principal<User>()!!.id.toString()
            val post = postService.getPostById(id)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            if (post.user.keycloakId != userId) {
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
            val user = call.principal<User>()!!.id.toString()
            if (user != userId) {
                call.respond(HttpStatusCode.Unauthorized, "User id not match")
            }

            val posts = postService.getPostsOfUserByCriteria(userId = userId)
            call.respond(posts.map { it.toResponse() })
        }

        post("/posts/{id}/likes") {
            val id = call.parameters["id"]!!
            val userId = call.principal<User>()!!.id
            if (userId.toString() != id) {
                call.respond(HttpStatusCode.Unauthorized, "User id not match")
            }
            postService.postRepo.likePost(userId, UUID.fromString(id))
        }
    }
}
