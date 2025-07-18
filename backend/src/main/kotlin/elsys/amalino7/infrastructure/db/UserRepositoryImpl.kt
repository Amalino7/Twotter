package elsys.amalino7.infrastructure.db

import elsys.amalino7.features.user.User
import elsys.amalino7.features.user.UserRepository
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.r2dbc.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class UserRepositoryImpl : UserRepository {
    override suspend fun getAll(input: PageRequest): PageResult<User> {
        return query {
            val users = Users.selectAll()
                .map { it.toUser() }
                .toList()
            PageResult(users, null)
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

    override suspend fun getFollowersById(
        id: Uuid,
        input: PageRequest
    ): PageResult<User> = query {
        val followers = Follows
            .join(Users, JoinType.LEFT, Follows.follower, Users.id)
            .selectAll()
            .where { Follows.followee eq id.toJavaUuid() }
            .offset(input.size * (input.page - 1))
            .limit(input.size)
            .map { it.toUser() }
            .toList()
        val totalCount = Follows
            .select(Follows.follower.count())
            .where(Follows.followee eq id.toJavaUuid())
            .single()[Follows.follower.count()]
        return@query PageResult(followers, totalCount)
    }

    override suspend fun getFollowingById(
        id: Uuid,
        input: PageRequest
    ): PageResult<User> = query {
        val followers = Follows
            .join(Users, JoinType.LEFT, Follows.followee, Users.id)
            .selectAll()
            .where { Follows.follower eq id.toJavaUuid() }
            .offset(input.size * (input.page - 1))
            .limit(input.size)
            .map { it.toUser() }
            .toList()
        val totalCount = Follows
            .select(Follows.followee.count())
            .where(Follows.follower eq id.toJavaUuid())
            .single()[Follows.follower.count()]
        return@query PageResult(followers, totalCount)
    }


    override suspend fun addFollowerForUser(userId: Uuid, followerId: Uuid): Boolean = query {
        Follows.insert {
            it[follower] = followerId.toJavaUuid()
            it[followee] = userId.toJavaUuid()
        }.insertedCount > 0
    }

    override suspend fun deleteFollowerForUser(userId: Uuid, followerId: Uuid): Boolean = query {
        Follows.deleteWhere { (Follows.follower eq followerId.toJavaUuid()) and (Follows.followee eq userId.toJavaUuid()) } > 0
    }
}

