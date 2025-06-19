package elsys.amalino7.security

import io.ktor.server.config.*

data class SecurityConfig(
    val keycloakDomain: String,
    val clientId: String,
    val clientSecret: String,
    val jwtRealm: String,
    val audience: String,
    val azp: String, // authorized party
    val callbackUrl: String,
    val frontendLoginSuccessUrl: String
)

fun ApplicationConfig.toSecurityConfig(): SecurityConfig {
    val keycloakConfig = config("ktor.security.keycloak")
    return SecurityConfig(
        keycloakDomain = keycloakConfig.property("domain").getString(),
        clientId = keycloakConfig.property("clientId").getString(),
        clientSecret = keycloakConfig.property("clientSecret").getString(),
        jwtRealm = keycloakConfig.property("realm").getString(),
        audience = keycloakConfig.property("audience").getString(),
        azp = keycloakConfig.property("azp").getString(),
        callbackUrl = keycloakConfig.property("callbackUrl").getString(),
        frontendLoginSuccessUrl = keycloakConfig.property("frontendLoginSuccessUrl").getString()
    )
}