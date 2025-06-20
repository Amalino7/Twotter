package elsys.amalino7.plugins

import DatabaseSeeder
import elsys.amalino7.seeding.KeycloakConfig
import io.ktor.server.application.*

fun Application.Seeding() {
    val config = environment.config.config("ktor.security.keycloak")
    DatabaseSeeder.seedProcedurally(
        KeycloakConfig(
            config.property("domain").getString(),
            config.property("realm").getString(),
            "admin",
            "admin"
        )
    )
}