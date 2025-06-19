package elsys.amalino7.seeding

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class KeycloakUser(
    val username: String,
    val email: String,
    val enabled: Boolean = true,
    val credentials: List<Credential> = emptyList()
)

@Serializable
data class Credential(
    val type: String = "password",
    var value: String,
    val temporary: Boolean = false
)

@Serializable
data class AccessTokenResponse(
    val access_token: String
)

class KeycloakAdminClient(private val config: KeycloakConfig) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun getAdminAccessToken(): String {
        val response = client.submitForm(
            url = "${config.serverUrl.dropLast(16)}/realms/master/protocol/openid-connect/token",
            formParameters = Parameters.build {
                append("client_id", "admin-cli")
                append("username", config.adminUsername)
                append("password", config.adminPassword)
                append("grant_type", "password")
            }
        )
        println(response.bodyAsText())
        return response.body<AccessTokenResponse>().access_token
    }

    suspend fun createUser(user: KeycloakUser): String? {
        val token = getAdminAccessToken()
        val response = client.post("${config.serverUrl.dropLast(16)}/admin/realms/${config.realm}/users") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        // Keycloak returns the user's ID in the Location header
        return response.headers[HttpHeaders.Location]?.substringAfterLast("/")
    }
}

data class KeycloakConfig(
    val serverUrl: String,
    val realm: String,
    val adminUsername: String,
    val adminPassword: String
)