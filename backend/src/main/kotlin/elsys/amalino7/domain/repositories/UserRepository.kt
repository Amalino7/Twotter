package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.User

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): User
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUserById(id: String): Boolean
}