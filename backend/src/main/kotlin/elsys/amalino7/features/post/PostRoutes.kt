package elsys.amalino7.features.post

import elsys.amalino7.features.user.User
import elsys.amalino7.utils.Direction
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.Sort
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.Uuid

fun Route.postRoutes(postService: PostService) {
    route("/posts") {
        get {
            val page = call.queryParameters["page"]?.toLong() ?: 1
            val size = call.queryParameters["size"]?.toInt() ?: 10
            val sortProperty = call.queryParameters["sort"] ?: ""
            val sortDirection = when (call.queryParameters["dir"]) {
                "ASC" -> Direction.ASC
                "DESC" -> Direction.DESC
                else -> Direction.NONE
            }
            val sort = Sort(sortProperty, sortDirection)
            val pageRequest = PageRequest(page, size, sort)
            val posts = postService.getPosts(pageRequest)
            call.respond(HttpStatusCode.OK, posts)
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