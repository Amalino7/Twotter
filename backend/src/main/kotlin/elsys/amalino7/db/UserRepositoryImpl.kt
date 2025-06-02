package elsys.amalino7.db

import Users
import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.repositories.UserRepository
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.*


class UserRepositoryImpl : UserRepository {

    override suspend fun getUserById(id: UUID): User? {
        return transaction {
            Users.selectAll().where { Users.id eq id }
                .singleOrNull()?.toUser()
        }
    }

//        override suspend fun getUserByEmail(id: String)

    override suspend fun getAllUsers(): List<User> {
        return transaction {
            Users.selectAll()
                .map { it.toUser() }
        }
    }

    override suspend fun addUser(user: User): User {
        return transaction {
            Users.insertReturning {
                it[id] = user.id
                it[username] = user.name
                it[email] = user.email
                it[bio] = user.bio
                it[displayName] = user.displayName
                it[keycloakId] = user.keycloakId
            }.single().toUser()
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return transaction {
            Users.update({ Users.id eq user.id }) {
                it[username] = user.name
                it[email] = user.email
                it[bio] = user.bio
                it[displayName] = user.displayName
            } > 0
        }
    }

    override suspend fun deleteUserById(id: UUID): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }
}