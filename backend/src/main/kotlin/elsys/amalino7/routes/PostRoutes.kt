package elsys.amalino7.routes

import elsys.amalino7.domain.services.PostService
import elsys.amalino7.dto.PostCreateRequest
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
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
    post("/posts") {
        val post = call.receive<PostCreateRequest>()
        val returnedPost = postService.createPost(post)
        return@post call.respond(returnedPost)
    }

    delete("/posts/{id}") {
        val id = call.parameters["id"]!!
        val isDeleted = postService.deletePostById(id)
        if (isDeleted) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    get("/users/{id}/posts") {
        val userId = call.parameters["id"]!!
        val posts = postService.getPostsOfUser(userId = userId)
        call.respond(posts.map { it.toResponse() })
    }
    get("/users/{id}/feed") {
        val userId = call.parameters["id"]!!
        val posts = postService.getPostsOfUserByCriteria(userId = userId)
        call.respond(posts.map { it.toResponse() })
    }
}