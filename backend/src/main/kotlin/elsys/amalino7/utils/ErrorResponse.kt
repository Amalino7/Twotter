package elsys.amalino7.utils

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val code: String, val message: String)
