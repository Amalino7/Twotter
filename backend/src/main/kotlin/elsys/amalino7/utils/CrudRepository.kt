package elsys.amalino7.utils

interface CrudRepository<ID, T> {
    suspend fun getAll(input: PageRequest): PageResult<T>
    suspend fun create(model: T): T
    suspend fun getById(id: ID): T?
    suspend fun update(model: T): T
    suspend fun delete(id: ID): Boolean
}

abstract class CrudService<ID, T>(
    private val crudRepository: CrudRepository<ID, T>
) {
    suspend fun getAll(): PageResult<T> = crudRepository
        .getAll(PageRequest(1, 100, Sort("time", Direction.NONE)))

    suspend fun getById(id: ID): T? = crudRepository.getById(id)
    suspend fun create(t: T): T = crudRepository.create(t)
    suspend fun update(t: T): T = crudRepository.update(t)
    suspend fun delete(t: ID): Boolean = crudRepository.delete(t)
}
