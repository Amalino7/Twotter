package elsys.amalino7.routes

import elsys.amalino7.db.PostRepositoryImpl
import elsys.amalino7.domain.model.Post
import elsys.amalino7.dto.PostCreateRequest
import elsys.amalino7.dto.toPost
import elsys.amalino7.dto.toResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.postRoute() {
    get("/posts") {
        val PostDTOs = PostRepositoryImpl().getAllPosts().map(Post::toResponse)
        call.respond(PostDTOs)

    }
    get("/posts/{id}") {
        val id = call.parameters["id"]!!
        val response = PostRepositoryImpl().getPostById(UUID.fromString(id))?.toResponse()
        call.respond(response ?: HttpStatusCode.NotFound)
    }
    post("/posts") {
        val post = call.receive<PostCreateRequest>()
        val returnedPost = PostRepositoryImpl().addPost(post.toPost())
        return@post call.respond(returnedPost.toResponse())
    }
    delete("/posts/{id}") {
        val id = call.parameters["id"]!!
        val isDeleted = PostRepositoryImpl().deletePostById(UUID.fromString(id))
        if (isDeleted) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    get("/users/{id}/posts") {
        val userId = call.parameters["id"]!!
        val posts = PostRepositoryImpl().getPostsOfUser(userId = UUID.fromString(userId))
        call.respond(posts.map { it.toResponse() })
    }
    get("/users/{id}/feed") {
        val userId = call.parameters["id"]!!
        val posts = PostRepositoryImpl().getPostsOfUserByCriteria(userId = UUID.fromString(userId))
        call.respond(posts.map { it.toResponse() })
    }
}