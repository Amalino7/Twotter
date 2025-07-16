package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.user.User
import elsys.amalino7.features.user.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class UserRepositoryImpl : UserRepository {
    override suspend fun getAll(): List<User> {
        return query {
            Users.selectAll()
                .map { it.toUser() }
                .toList()
        }
    }

    override suspend fun getUserByKeycloakId(keycloakId: String): User? = query {
        Users
            .selectAll()
            .where { Users.keycloakId eq keycloakId }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun create(model: User): User {
        return query {
            Users.insertReturning {
                it[id] = model.id.toJavaUuid()
                it[username] = model.name
                it[email] = model.email
                it[bio] = model.bio
                it[displayName] = model.displayName
                it[keycloakId] = model.keycloakId
                it[createdAt] = model.createdAt
                it[updatedAt] = model.updatedAt
            }.single().toUser()
        }
    }

    override suspend fun getById(id: Uuid): User? = query {
        Users
            .selectAll()
            .where { Users.id eq id.toJavaUuid() }
            .singleOrNull()?.toUser()
    }

    override suspend fun update(
        model: User
    ): User = query {
        Users.update({ Users.id eq model.id.toJavaUuid() }) {
            it[username] = model.name
            it[email] = model.email
            it[bio] = model.bio
            it[displayName] = model.displayName
            it[keycloakId] = model.keycloakId
            it[createdAt] = model.createdAt
            it[updatedAt] = model.updatedAt
        }
        model
    }

    override suspend fun delete(id: Uuid): Boolean = query {
        Users.deleteWhere { Users.id eq id.toJavaUuid() } > 0
    }

    override suspend fun getFollowersById(id: Uuid): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowingById(id: Uuid): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun addFollowerForUser(userId: Uuid, followerId: Uuid): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFollowerForUser(userId: Uuid, followerId: Uuid): Boolean {
        TODO("Not yet implemented")
    }
}

