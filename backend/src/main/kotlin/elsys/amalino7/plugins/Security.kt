package elsys.amalino7.plugins

import com.auth0.jwk.UrlJwkProvider
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.net.URL

val jwtDomain = "http://localhost:7080/realms/KtorAuth"
val jwtRealm = "KtorAuth"
val jwkProvider = UrlJwkProvider(URL("$jwtDomain/protocol/openid-connect/certs"))

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
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(jwkProvider, jwtDomain) {
                withAudience("ktor")
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
            get("/login") {
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

                    call.response.cookies.append(
                        Cookie(
                            name = "refresh_token",
                            value = principal.refreshToken.toString(),
//                            httpOnly = true,
//                            secure = true,
                            path = "/refresh",
                            maxAge = 3600 * 24 * 7,
                        )
                    )

                    call.respondText("User info:\n${res} Access token: $accessToken\n Refresh token: ${principal.refreshToken}")

                } else {
                    call.respond(HttpStatusCode.Unauthorized, "No token")
                }


            }
        }
        post("/logout")
        {
            call.response.cookies.append(
                "refresh_token", "", CookieEncoding.URI_ENCODING, 0, GMTDate.START
            )
            call.respond(HttpStatusCode.OK)
        }
        post("/refresh")
        {
            val refreshToken =
                call.request.cookies["refresh_token"] ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val httpClient = HttpClient(Apache) {}
            val response: HttpResponse = httpClient.submitForm(
                url = "$jwtDomain/protocol/openid-connect/token",
                formParameters = Parameters.build {
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                    append("client_id", "ktor")
                    append("client_secret", dotenv().get("CLIENT_SECRET"))
                }
            )

            if (!response.status.isSuccess()) {
                call.respond(HttpStatusCode.Unauthorized, "Refresh failed")
                return@post
            }

            val tokenResponse = response.body<TokenResponse>()

            call.response.cookies.append(
                Cookie(
                    name = "refresh_token",
                    value = tokenResponse.refresh_token,
                    httpOnly = true,
                    secure = true,
                    path = "/refresh",
                    maxAge = 3600 * 24 * 7,
                )
            )

            call.respond(
                mapOf(
                    "access_token" to tokenResponse.access_token
                )
            )
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
//    val username: String
)

@Serializable
data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val token_type: String,
    val scope: String
)

