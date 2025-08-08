package elsys.amalino7.infrastructure.db

import io.ktor.server.application.*
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.Schema
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


@OptIn(ExperimentalDatabaseMigrationApi::class)
fun Application.connectToDatabase() {
    R2dbcDatabase.connect(
        url = "r2dbc:postgresql://localhost:5433/postgres",
        databaseConfig = {
            connectionFactoryOptions {
                option(ConnectionFactoryOptions.USER, "postgres")
                option(ConnectionFactoryOptions.PASSWORD, "postgres")
            }
            defaultSchema = Schema(
                name = "test",
            )
        }

    )

    runBlocking {
        suspendTransaction {
            val schema = Schema("test")
            SchemaUtils.createSchema(schema)
            SchemaUtils.setSchema(schema)
            SchemaUtils.create(Users, Comments, Posts, Follows, Likes, Images)
        }
    }

}