package elsys.amalino7.utils

interface CrudRepository<ID, T> {
    suspend fun getAll(): List<T>
    suspend fun create(model: T): T
    suspend fun getById(id: ID): T?
    suspend fun update(model: T): T
    suspend fun delete(id: ID): Boolean
}

abstract class CrudService<ID, T>(
    private val crudRepository: CrudRepository<ID, T>
) {
    suspend fun getAll(): List<T> = crudRepository.getAll()
    suspend fun getById(id: ID): T? = crudRepository.getById(id)
    suspend fun create(t: T): T = crudRepository.create(t)
    suspend fun update(t: T): T = crudRepository.update(t)
    suspend fun delete(t: ID): Boolean = crudRepository.delete(t)
}
