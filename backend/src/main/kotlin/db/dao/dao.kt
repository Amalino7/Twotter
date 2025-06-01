package com.example.db.dao

import Follows
import Posts
import Users
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*


class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.username
    var email by Users.email
    var passwordHash by Users.passwordHash
    var displayName by Users.displayName
    var bio by Users.bio
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt

    val posts by Post referrersOn Posts.user
    val followers by User via Follows
    val following by User via Follows
}

class Post(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Post>(Posts)

    var user by User referencedOn Posts.user
    var content by Posts.content
    var createdAt by Posts.createdAt
    var updatedAt by Posts.updatedAt
}