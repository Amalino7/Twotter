package elsys.amalino7.features.user

import elsys.amalino7.utils.CrudService
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlin.uuid.Uuid

class UserService(val userRepository: UserRepository) : CrudService<Uuid, User>(
    userRepository
) {
    suspend fun getUserByKeycloakId(keycloakId: String): User? {
        return userRepository.getUserByKeycloakId(keycloakId)
    }

    suspend fun getFollowersById(id: Uuid, input: PageRequest): PageResult<User> =
        userRepository.getFollowersById(id, input)

    suspend fun getFollowingById(id: Uuid, input: PageRequest): PageResult<User> =
        userRepository.getFollowingById(id, input)

    suspend fun addFollowerForUser(userId: Uuid, followerId: Uuid): Boolean =
        userRepository.addFollowerForUser(userId, followerId)

    suspend fun deleteFollowerForUser(userId: Uuid, followerId: Uuid): Boolean =
        userRepository.deleteFollowerForUser(userId, followerId)
}