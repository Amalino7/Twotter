package com.example.routes

import com.example.db.PostRepositoryImpl
import com.example.models.Post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postRoute() {
    get("/posts") {

        val Post = PostRepositoryImpl().getAll()
        val PostDto = Post.map { Post(it.content, "") }
        call.respond(PostDto)
    }
    post("/posts") {
        val post = call.receive<Post>()
    }
}