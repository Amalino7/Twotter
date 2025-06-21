package elsys.amalino7.routes

import Images
import elsys.amalino7.minio.MinioClientInstance
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.jetbrains.exposed.v1.jdbc.insert
import query
import java.util.*

fun Route.imageRoutes() {
    route("/images") {
        post("/upload") {
            var originalFileName = ""
            var contentType = ""
            var fileBytes: ByteArray? = null

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        originalFileName = part.originalFileName as String
                        contentType = part.contentType.toString()
                        fileBytes = part.provider().toByteArray()
                    }

                    else -> {}
                }
                part.dispose()
            }

            if (fileBytes != null) {
                val minioObjectKey = "${UUID.randomUUID()}-${originalFileName}"

                try {
                    // Upload to Minio
                    MinioClientInstance.uploadImage(minioObjectKey, fileBytes!!.inputStream(), contentType)

                    // Save metadata to database
                    val imageUrl = MinioClientInstance.getImageUrl(minioObjectKey)
                    query {
                        Images.insert {
                            it[Images.originalFileName] = originalFileName
                            it[Images.minioObjectKey] = minioObjectKey
                            it[Images.contentType] = contentType
                        }
                    }

                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("imageUrl" to imageUrl, "minioObjectKey" to minioObjectKey)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Failed to upload image: ${e.message}")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "No file uploaded")
            }
        }
    }
}
