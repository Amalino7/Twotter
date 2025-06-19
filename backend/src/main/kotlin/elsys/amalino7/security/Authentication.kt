package elsys.amalino7.security

import com.auth0.jwk.UrlJwkProvider
import elsys.amalino7.domain.services.UserService
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.net.URL

fun AuthenticationConfig.customJwt(name: String, config: SecurityConfig, userService: UserService) {
    val jwkProvider = UrlJwkProvider(URL("${config.keycloakDomain}/protocol/openid-connect/certs"))

    jwt(name) {
        realm = config.jwtRealm
        verifier(jwkProvider, config.keycloakDomain) {
            withAudience(config.audience)
            withIssuer(config.keycloakDomain)
        }
        validate { credential ->
            val audience = credential.payload.audience
            val azp = credential.payload.getClaim("azp")?.asString()

            val isAudienceValid = audience.contains(config.audience)
            val isAzpValid = azp == config.azp

            if (isAudienceValid && isAzpValid) {
                credential.payload.subject?.let { keycloakId ->
                    userService.getUserByKeycloakId(keycloakId)
                }

            } else {
                null // Will result in 401 Unauthorized
            }
        }
        challenge { _, _ ->
            call.respond(HttpStatusCode.Unauthorized, "Token validation failed")
        }
    }
}

fun AuthenticationConfig.customOauth(name: String, config: SecurityConfig, client: HttpClient) {
    oauth(name) {
        urlProvider = { config.callbackUrl }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "keycloak",
                authorizeUrl = "${config.keycloakDomain}/protocol/openid-connect/auth",
                accessTokenUrl = "${config.keycloakDomain}/protocol/openid-connect/token",
                requestMethod = HttpMethod.Post,
                clientId = config.clientId,
                clientSecret = config.clientSecret,
                defaultScopes = listOf("openid", "profile", "email")
            )
        }
        this.client = client
    }
}