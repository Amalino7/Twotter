package elsys.amalino7.plugins

import com.auth0.jwk.UrlJwkProvider
import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.UserCreateRequest
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
                    defaultScopes = listOf("openid", "profile", "email")
                )
            }
            client = HttpClient(Apache)
        }

        val jwtDomain = "http://localhost:7080/realms/KtorAuth"
        val jwtRealm = "KtorAuth"
        val jwkProvider = UrlJwkProvider(URL("$jwtDomain/protocol/openid-connect/certs"))
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(jwkProvider, jwtDomain) {
                withAudience("ktor")
                withIssuer(jwtDomain)
            }
            validate { credential ->
                println("JWT validate triggered: issuer=${credential.payload.issuer}, audience=${credential.payload.audience}")
                if (credential.payload.audience.contains("ktor")) JWTPrincipal(credential.payload) else null
            }

            challenge { _, _ ->
                println("JWT challenge triggered: issuer=${jwtDomain}, audience=ktor")
                call.respond(HttpStatusCode.Unauthorized, "Token validation failed")
            }
        }
    }

    routing {
        authenticate("keycloakOAuth") {
            post("/logout") {
                val accessToken = call.principal<OAuthAccessTokenResponse.OAuth2>()?.accessToken
                val client = HttpClient(Apache) {
                    install(ContentNegotiation) {
                        json()
                    }
                }
                client.post("http://localhost:7080/realms/KtorAuth/protocol/openid-connect/logout") {
                    header("Authorization", "Bearer $accessToken")
                }
            }
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
                    client.close()

                    val user = UserService().getUserByKeycloakId(res.sub)
                    if (user == null) UserService().addUser(
                        UserCreateRequest(
                            res.preferred_username,
                            res.email,
                            res.sub
                        )
                    )
                    call.respondText("User info:\n${res} Access token: $accessToken\n Refresh token: ${principal.refreshToken}")

                } else {
                    call.respond(HttpStatusCode.Unauthorized, "No token")
                }
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KeycloakUserInfo(
    val sub: String,
    val email: String,
    val preferred_username: String,
)


