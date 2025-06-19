package elsys.amalino7.db

import Follows
import Users
import elsys.amalino7.domain.model.User
import elsys.amalino7.domain.repositories.UserRepository
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import query
import java.util.*


class UserRepositoryImpl : UserRepository {

    override suspend fun getUserById(id: UUID): User? {
        return query {
            Users.selectAll().where { Users.id eq id }
                .singleOrNull()?.toUser()
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return query {
            Users.selectAll()
                .map { it.toUser() }
        }
    }

    override suspend fun addUser(user: User): User {
        return query {
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
        return query {
            Users.update({ Users.id eq user.id }) {
                it[username] = user.name
                it[email] = user.email
                it[bio] = user.bio
                it[displayName] = user.displayName
            } > 0
        }
    }

    override suspend fun deleteUserById(id: UUID): Boolean {
        return query {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }

    override suspend fun getFollowersById(id: UUID): List<User> {
        return query {
            Follows
                .join(
                    Users, JoinType.INNER, onColumn = Follows.follower, otherColumn = Users.id
                )
                .selectAll().where { Follows.followee eq id }
                .map { it.toUser() }
        }
    }

    override suspend fun getFollowingById(id: UUID): List<User> {
        return query {
            Follows
                .join(
                    Users, JoinType.INNER, onColumn = Follows.followee, otherColumn = Users.id
                )
                .selectAll().where { Follows.follower eq id }
                .map { it.toUser() }
        }
    }

    override suspend fun addFollowerForUser(userId: UUID, followerId: UUID): Boolean {
        return query {
            Follows.insertReturning {
                it[this.follower] = followerId
                it[this.followee] = userId
            }.singleOrNull() != null
        }
    }

    override suspend fun getUserByKeycloakId(keycloakId: String): User? {
        return query {
            Users.selectAll()
                .where(Users.keycloakId eq keycloakId)
                .singleOrNull()?.toUser()
        }
    }

}