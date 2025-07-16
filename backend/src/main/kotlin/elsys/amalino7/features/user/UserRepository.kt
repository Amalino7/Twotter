package elsys.amalino7.features.user

import elsys.amalino7.utils.CrudRepository
import kotlin.uuid.Uuid


interface UserRepository : CrudRepository<Uuid, User> {
    suspend fun getFollowersById(id: Uuid): List<User>
    suspend fun getFollowingById(id: Uuid): List<User>
    suspend fun addFollowerForUser(userId: Uuid, followerId: Uuid): Boolean
    suspend fun getUserByKeycloakId(keycloakId: String): User?
    suspend fun deleteFollowerForUser(userId: Uuid, followerId: Uuid): Boolean
}