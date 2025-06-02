package elsys.amalino7.domain.services

import elsys.amalino7.db.UserRepositoryImpl
import elsys.amalino7.dto.UserCreateRequest
import elsys.amalino7.dto.toResponse
import elsys.amalino7.dto.toUser
import java.util.*


class UserService {
    suspend fun addUser(user: UserCreateRequest) =
        UserRepositoryImpl().addUser(user.toUser()).toResponse()

    suspend fun getAllUsers() = UserRepositoryImpl().getAllUsers().map { it.toResponse() }
    suspend fun deleteUser(id: String) = UserRepositoryImpl().deleteUserById(UUID.fromString(id))
    suspend fun getUserById(id: String) = UserRepositoryImpl().getUserById(UUID.fromString(id))?.toResponse()
    suspend fun createUser(user: UserCreateRequest) = UserRepositoryImpl().addUser(user.toUser()).toResponse()
}