package elsys.amalino7.features.images

import kotlin.uuid.Uuid

class ImageService() {
    suspend fun getImages(): List<Image> = emptyList()
    suspend fun getImageById(id: Uuid): Image? = null
}