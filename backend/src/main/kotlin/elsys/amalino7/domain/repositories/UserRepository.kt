package elsys.amalino7.domain.repositories

import elsys.amalino7.domain.model.User
import java.util.*

interface UserRepository {
    suspend fun getUserById(id: UUID): User?
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): User
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUserById(id: UUID): Boolean
}