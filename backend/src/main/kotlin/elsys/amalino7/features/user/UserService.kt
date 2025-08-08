package elsys.amalino7.features.user

import elsys.amalino7.utils.*
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

    suspend fun getById(id: Uuid, currentUserId: Uuid? = null): UserResponse? {
        val user = userRepository.getById(id) ?: return null
        val isFollowed = if (currentUserId != null && currentUserId != id) {
            userRepository.isFollowing(currentUserId, id)
        } else {
            null
        }
        return user.toResponse(isFollowed)
    }

    suspend fun getAllWithFollowing(id: Uuid) {
        val users =
            userRepository.getAll(PageRequest(1, 10, Sort("a", Direction.NONE))).items.map { it.toResponse(null) }
        val ids = users.map { Uuid.parse(it.id) }
        val follows = userRepository.getFollowingStatusForUsers(id, ids)
        for (user in users) {
            user.isFollowed = follows[Uuid.parse(user.id)] ?: false
        }
    }

}