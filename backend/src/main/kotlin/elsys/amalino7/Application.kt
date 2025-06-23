package elsys.amalino7

import connectToDatabase
import elsys.amalino7.minio.minioClient
import elsys.amalino7.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    connectToDatabase()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureMonitoring()
    minioClient()

//    Seeding()
//    configureAdministration()
    configureRouting()
}
