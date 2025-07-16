package elsys.amalino7.features.user

import elsys.amalino7.utils.CrudService
import kotlin.uuid.Uuid

class UserService(val userRepository: UserRepository) : CrudService<Uuid, User>(
    userRepository
) {
    suspend fun getUserByKeycloakId(keycloakId: String): User? {
        return userRepository.getUserByKeycloakId(keycloakId)
    }
}