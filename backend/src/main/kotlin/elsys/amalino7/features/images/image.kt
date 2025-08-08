package elsys.amalino7.features.images

import kotlin.uuid.Uuid

data class Image(
    val id: String,
    val url: String,
    val time: String,
    val uploaderId: Uuid
)