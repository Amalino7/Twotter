package elsys.amalino7.plugins

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    authentication {
        oauth("keycloakOAuth") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "http://localhost:7080/realms/Ktor Auth/protocol/openid-connect/auth",
                    accessTokenUrl = "http://localhost:7080/realms/Ktor Auth/protocol/openid-connect/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "ktor backend",
                    clientSecret = "1inPVKbpeQVpUPFQNNZxfRZf3XlTMDMK", // TODO fix
                    defaultScopes = listOf("openid", "profile", "email")
                )
            }
            client = HttpClient(Apache)
        }
    }
//    // Please read the jwt property from the config file if you are using EngineMain
//    val jwtAudience = "jwt-audience"
//    val jwtDomain = "https://jwt-provider-domain/"
//    val jwtRealm = "ktor sample app"
//    val jwtSecret = "secret"
//    authentication {
//        jwt {
//            realm = jwtRealm
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(jwtSecret))
//                    .withAudience(jwtAudience)
//                    .withIssuer(jwtDomain)
//                    .build()
//            )
//            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
//            }
//        }
//    }
    routing {
        authenticate("keycloakOAuth") {
            get("login") {
                call.respondRedirect("/callback")
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                if (principal != null) {
                    val accessToken = principal.accessToken
                    call.respondText("Access token: $accessToken")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "No token")
                }
            }
        }
    }
}


