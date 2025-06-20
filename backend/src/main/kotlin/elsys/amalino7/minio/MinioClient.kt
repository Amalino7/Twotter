package elsys.amalino7.minio

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.SetBucketPolicyArgs
import java.io.InputStream

//object MinioClient {
//


private const val BUCKET_NAME = "images" // Your bucket name

private const val policyJson = """{
   "Version":"2012-10-17",
   "Statement":[
      {
         "Effect":"Allow",
         "Principal":"*",
         "Action":["s3:GetObject"],
         "Resource":["arn:aws:s3:::${BUCKET_NAME}*"]
      }
   ]
}"""

object MinioClientInstance {
    var minioClient: MinioClient? = null
    var url: String = ""
    fun startClient(config: ApplicationConfig) {
        minioClient = MinioClient.builder()
            .endpoint(config.property("url").getString()) // Your Minio server URL
            .credentials(
                config.property("access_key").getString(),
                config.property("secret_key").getString()
            ) // Your Minio access and secret keys
            .build()
        url = config.property("url").getString()
    }

    fun uploadImage(objectName: String, inputStream: InputStream, contentType: String) {
        // Ensure the bucket exists
        if (!minioClient!!.bucketExists(
                io.minio.BucketExistsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .build()
            )
        ) {
            println("Creating bucket")
            minioClient!!.makeBucket(io.minio.MakeBucketArgs.builder().bucket(BUCKET_NAME).build())
            minioClient!!.setBucketPolicy(
                SetBucketPolicyArgs
                    .builder()
                    .bucket(BUCKET_NAME)
                    .config(policyJson)
                    .build()
            )
        } else {
            println("Bucket already exists")
        }

        minioClient!!.putObject(
            PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .`object`(objectName)
                .stream(inputStream, -1, 10485760) // -1 for unknown size, 10MB part size
                .contentType(contentType)
                .build()
        )
    }

    fun getImageUrl(objectName: String): String {
        return "${url}/$BUCKET_NAME/$objectName"
    }
}

fun Application.minioClient() {
    val config = environment.config.config("ktor.minio")
    MinioClientInstance.startClient(config)
}
