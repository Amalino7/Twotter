package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.User
import elsys.amalino7.dto.UserPatchRequest
import java.util.*

interface UserRepository {
    suspend fun getUserById(id: UUID): User?
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): User
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUserById(id: UUID): Boolean
    suspend fun getFollowersById(id: UUID): List<User>
    suspend fun getFollowingById(id: UUID): List<User>
    suspend fun addFollowerForUser(userId: UUID, followerId: UUID): Boolean
    suspend fun getUserByKeycloakId(keycloakId: String): User?
    suspend fun deleteFollowerForUser(userId: UUID, followerId: UUID): Boolean
    suspend fun updateUser(id: UUID, userPatch: UserPatchRequest): User?
}