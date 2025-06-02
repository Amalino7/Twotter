package elsys.amalino7.routes

import elsys.amalino7.db.PostRepositoryImpl
import elsys.amalino7.domain.model.Post
import elsys.amalino7.dto.PostDTO
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postRoute() {
    get("/posts") {
        val PostDTOs = PostRepositoryImpl().getAllPosts().map { PostDTO(it.content, it.userID) }
        call.respond(PostDTOs)

    }
    post("/posts") {
        val post = call.receive<PostDTO>();
        val returnedPost = PostRepositoryImpl().addPost(Post(post.content, post.userID))
        return@post call.respond(PostDTO(returnedPost.content, returnedPost.userID))
    }
}