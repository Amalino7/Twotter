package elsys.amalino7.domain.services

import elsys.amalino7.domain.repositories.UserRepository
import elsys.amalino7.dto.UserCreateRequest
import elsys.amalino7.dto.toResponse
import elsys.amalino7.dto.toUser
import java.util.*


class UserService(private val userRepository: UserRepository) {
    suspend fun addUser(user: UserCreateRequest) =
        userRepository.addUser(user.toUser()).toResponse()

    suspend fun getAllUsers() = userRepository.getAllUsers().map { it.toResponse() }
    suspend fun deleteUser(id: String) = runCatching {
        userRepository.deleteUserById(UUID.fromString(id))
    }.getOrDefault(false)

    suspend fun getUserById(id: String) = runCatching {
        userRepository.getUserById(UUID.fromString(id))?.toResponse()
    }.getOrNull()

    suspend fun getUserByKeycloakId(id: String) = userRepository.getUserByKeycloakId(id)
    suspend fun getFollowersById(userId: UUID) = userRepository.getFollowersById(userId).map { it.toResponse() }
    suspend fun getFollowingById(userId: UUID) = userRepository.getFollowingById(userId).map { it.toResponse() }
    suspend fun updateUser(user: UserCreateRequest) = userRepository.updateUser(
        user.toUser()
    )

    suspend fun addFollowerForUser(userId: UUID, followerId: UUID) =
        userRepository.addFollowerForUser(userId, followerId)
}