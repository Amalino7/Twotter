//import elsys.amalino7.dto.UserCreateRequest
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.routing.*
//import io.ktor.server.testing.*
//import junit.framework.TestCase.assertTrue
//import java.util.*
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//class UserRoutesKtTest {
//
////    private val testUserRepo = InMemoryUserRepository()
//
//    @Test
//    fun `test GET all users`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
//        testUserRepo.create(UserCreateRequest("Gregory", "House", "house@hospital.com"))
//
//        val response = client.get("/users")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertTrue(response.bodyAsText().contains("Gregory"))
//    }
//
//    @Test
//    fun `test GET user by ID - found`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
//        val user = testUserRepo.create(UserCreateRequest("Lisa Cuddy", "cuddy@hospital.com"))
//
//        val response = client.get("/users/${user.id}")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertTrue(response.bodyAsText().contains("Lisa"))
//    }
//
//    @Test
//    fun `test GET user by ID - not found`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
//        val response = client.get("/users/999")
//        assertEquals(HttpStatusCode.NotFound, response.status)
//    }
//
//    @Test
//    fun `test POST create user`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
//        val response = client.post("/users") {
//            contentType(ContentType.Application.Json)
//            setBody("""{"firstName":"James","lastName":"Wilson","email":"wilson@hospital.com"}""")
//        }
//
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertTrue(response.bodyAsText().contains("Wilson"))
//    }
//
//    @Test
//    fun `test DELETE user - success`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
////        val user = testUserRepo.create(UserCreateRequest("Robert", "Chase", "chase@hospital.com"))
//
////        val response = client.delete("/users/${user.id}")
//        assertEquals(HttpStatusCode.OK, response.status)
//    }
//
//    @Test
//    fun `test DELETE user - not found`() = testApplication {
//        application {
//            routing {
//                userRoute()
//            }
//        }
//
//        val response = client.delete("/users/999")
//        assertEquals(HttpStatusCode.NotFound, response.status)
//    }
//}
