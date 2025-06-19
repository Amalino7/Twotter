package elsys.amalino7

import DatabaseSeeder
import connectToDatabase
import elsys.amalino7.plugins.*
import elsys.amalino7.seeding.KeycloakConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectToDatabase()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureMonitoring()
    val config = environment.config.config("ktor.security.keycloak")
    DatabaseSeeder.seedProcedurally(
        KeycloakConfig(
            config.property("domain").getString(),
            config.property("realm").getString(),
            "admin",
            "admin"
        )
    )
//    configureAdministration()
    configureRouting()
}
