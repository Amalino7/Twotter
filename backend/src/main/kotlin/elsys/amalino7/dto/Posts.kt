package elsys.amalino7.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDTO(
    val content:String,
    val userID: String
)