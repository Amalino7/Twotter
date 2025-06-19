package elsys.amalino7.security

import elsys.amalino7.domain.services.UserService
import elsys.amalino7.dto.KeycloakUserInfo
import elsys.amalino7.dto.TokenResponse
import elsys.amalino7.dto.UserCreateRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.authRouting(config: SecurityConfig, httpClient: HttpClient, userService: UserService) {

    authenticate("keycloakOAuth") {
        get("/login") {
            // This route will automatically redirect to Keycloak for login.
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "OAuth principal not found.")
                return@get
            }

            val userInfo = fetchUserInfo(httpClient, config.keycloakDomain, principal.accessToken)

            // Create user in local DB if it's their first login
            val user = userService.getUserByKeycloakId(userInfo.sub)
            if (user == null) {
                userService.addUser(
                    UserCreateRequest(
                        userInfo.preferred_username,
                        userInfo.email,
                        userInfo.sub
                    )
                )
            }

            call.response.cookies.append(createRefreshTokenCookie(principal.refreshToken.toString()))

            val redirectUrl = URLBuilder(config.frontendLoginSuccessUrl).apply {
                parameters.append("access_token", principal.accessToken)
            }.buildString()

            call.respond(principal.accessToken)
//            call.respondRedirect(redirectUrl)
        }
    }

    post("/logout") {
        // Expire the refresh token cookie
        call.response.cookies.append("refresh_token", "", maxAge = 0)
        call.respond(HttpStatusCode.OK, "Logged out")
    }

    post("/refresh") {
        val refreshToken = call.request.cookies["refresh_token"]
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Refresh token not found")

        try {
            val tokenResponse = exchangeRefreshToken(httpClient, config, refreshToken)
            call.response.cookies.append(createRefreshTokenCookie(tokenResponse.refresh_token))
            call.respond(mapOf("access_token" to tokenResponse.access_token))
        } catch (e: Exception) {
            call.application.log.error("Token refresh failed", e)
            call.respond(HttpStatusCode.Unauthorized, "Refresh failed")
        }
    }
}

private suspend fun fetchUserInfo(client: HttpClient, domain: String, token: String): KeycloakUserInfo {
    return client.get("$domain/protocol/openid-connect/userinfo") {
        header(HttpHeaders.Authorization, "Bearer $token")
    }.body()
}

private suspend fun exchangeRefreshToken(
    client: HttpClient,
    config: SecurityConfig,
    refreshToken: String
): TokenResponse {
    val response: HttpResponse = client.submitForm(
        url = "${config.keycloakDomain}/protocol/openid-connect/token",
        formParameters = Parameters.build {
            append("grant_type", "refresh_token")
            append("refresh_token", refreshToken)
            append("client_id", config.clientId)
            append("client_secret", config.clientSecret)
        }
    )

    if (!response.status.isSuccess()) {
        throw IllegalStateException("Failed to refresh token: ${response.bodyAsText()}")
    }
    return response.body()
}

private fun createRefreshTokenCookie(value: String): Cookie = Cookie(
    name = "refresh_token",
    value = value,
    httpOnly = true,
    secure = true, // Set to false in dev if not using HTTPS
    path = "/refresh",
    maxAge = 60 * 60 * 24 * 7 // 1 week
)