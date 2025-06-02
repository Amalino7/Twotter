package elsys.amalino7.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String, val name: String, val email: String
)