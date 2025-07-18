package elsys.amalino7.security

import com.auth0.jwk.UrlJwkProvider
import elsys.amalino7.features.user.UserCreateRequest
import elsys.amalino7.features.user.UserService
import elsys.amalino7.features.user.toUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import java.net.URL

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val userService by inject<UserService>()
    val jwkProvider = UrlJwkProvider(URL("http://localhost:7080/realms/TwotterFrontend/protocol/openid-connect/certs"))
    val audience = "account"
    val issuer = "http://localhost:7080/realms/TwotterFrontend"
    val jwtRealm = "TwotterFrontend"
    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(jwkProvider, issuer) {
                withAudience(audience)
                withIssuer(issuer)
            }
            validate { credential ->
                println("Validate triggered")
                val azp = credential.payload.getClaim("azp")?.asString()
                if (azp == "twotter") {
                    credential.payload.subject?.let { keycloakId ->
                        var user = userService.getUserByKeycloakId(keycloakId)
                        if (user == null) {
                            user = userService.create(
                                UserCreateRequest(
                                    credential.payload.getClaim("name").asString(),
                                    credential.payload.getClaim("email").asString(),
                                    keycloakId
                                ).toUser()
                            )
                        }
                        user
                    }
                } else {
                    null // Will result in 401 Unauthorized
                }
            }
            challenge { arg1, arg2 ->
                println("Challenge triggered")
                println("$arg1, $arg2")
                call.respond(HttpStatusCode.Unauthorized, "Token validation failed")
            }
        }
    }
}