package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val content: String,
    val mediaUrl: String,
)