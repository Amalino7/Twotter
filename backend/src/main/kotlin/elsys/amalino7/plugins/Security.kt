package elsys.amalino7.plugins

import elsys.amalino7.db.UserRepositoryImpl
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.security.authRouting
import elsys.amalino7.security.customJwt
import elsys.amalino7.security.customOauth
import elsys.amalino7.security.toSecurityConfig
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    // 1. Load configuration in a type-safe way
    val securityConfig = environment.config.toSecurityConfig()

    // 2. Create a single, reusable HttpClient instance
    val httpClient = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }

    // 3. Instantiate services (ideally using a dependency injection framework)
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)

    // 4. Configure Ktor's authentication plugin

    install(Authentication) {
        customJwt("auth-jwt", securityConfig, userService)
        customOauth("keycloakOAuth", securityConfig, httpClient)
    }

    routing {
        route("/auth") {
            authRouting(securityConfig, httpClient, userService)
        }
    }

}