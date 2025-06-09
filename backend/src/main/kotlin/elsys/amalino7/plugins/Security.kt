package elsys.amalino7.plugins

import com.auth0.jwk.UrlJwkProvider
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.net.URL
import java.security.interfaces.RSAPublicKey

fun Application.configureSecurity() {
    authentication {
        oauth("keycloakOAuth") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "keycloak",
                    authorizeUrl = "http://localhost:7080/realms/KtorAuth/protocol/openid-connect/auth",
                    accessTokenUrl = "http://localhost:7080/realms/KtorAuth/protocol/openid-connect/token",

                    requestMethod = HttpMethod.Post,
                    clientId = "ktor",
                    clientSecret = dotenv().get("CLIENT_SECRET"),
                    defaultScopes = listOf("openid", "profile")
                )
            }
            client = HttpClient(Apache)
        }

        val jwtDomain = "http://localhost:7080/realms/KtorAuth"
        val jwtRealm = "KtorAuth"
        val jwkProvider = UrlJwkProvider(URL(jwtDomain))
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(jwkProvider, jwtDomain) {
                acceptLeeway(3)
                withAudience("ktor")
            }
            validate { credential ->
                if (credential.payload.audience.contains("ktor")) JWTPrincipal(credential.payload) else null
            }
        }
    }

    routing {
        authenticate("keycloakOAuth") {
            get("login") {
                // redirects
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

                if (principal != null) {
                    val accessToken = principal.accessToken

                    val client = HttpClient(Apache) {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                    val res: KeycloakUserInfo =
                        client.get("http://localhost:7080/realms/KtorAuth/protocol/openid-connect/userinfo")
                        {
                            header("Authorization", "Bearer $accessToken")
                        }.body()

                    call.respondText("User info:\n${res} Access token: $accessToken\n")

                } else {
                    call.respond(HttpStatusCode.Unauthorized, "No token")
                }
            }
        }
    }
}

fun publicKeyFromKeycloak(): RSAPublicKey {
    val jwkProvider = UrlJwkProvider(URL("http://localhost:7080/realms/KtorAuth"))
    val key = jwkProvider.get("public_key")
    return key.publicKey as RSAPublicKey
}

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KeycloakUserInfo(
    val sub: String,
    val email: String,
    val name: String
)


