package elsys.amalino7.features.post

import elsys.amalino7.features.user.User
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.Uuid

fun Route.postRoutes(postService: PostService) {
    route("/posts") {
        get {
            val post = postService.getPosts()
            call.respond(HttpStatusCode.OK, post)
        }
        get("/{id}")
        {
            val postId = call.parameters["id"]!!
            val post = postService.getById(Uuid.parse(postId))
            if (post == null) {
                call.respond(HttpStatusCode.NotFound, "post with id $postId not found")
            } else {
                call.respond(HttpStatusCode.OK, post)
            }
        }
        authenticate("auth-jwt") {
            post {
                val user = call.principal<User>()!!
                val post = postService.createPost(call.receive<PostCreateRequest>(), user)
                call.respond(HttpStatusCode.Created, post)
            }
            delete("/{id}") {
                val id = call.parameters["id"]!!
                val user = call.principal<User>()!!
                val success = postService.deletePost(id, user)
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post with id $id not found")
                }
            }
            patch("/{id}") {
                val id = call.parameters["id"]!!
                val newPost = call.receive<PostPatchRequest>()
                val user = call.principal<User>()!!
                val post = postService.updatePost(newPost, id, user)
                call.respond(HttpStatusCode.OK, post)
            }
        }
    }
    get("/users/{userId}/posts") {
        val userId = call.parameters["userId"]!!
        val posts = postService.getPostsOfUser(Uuid.parse(userId), call.principal<User>()?.id)
        call.respond(HttpStatusCode.OK, posts)
    }
    authenticate("auth-jwt") {
        get("/feed") {
            val userId = call.principal<User>()!!.id
            val posts = postService.getFeedOfUser(userId)
            call.respond(HttpStatusCode.OK, posts)
        }
    }
}