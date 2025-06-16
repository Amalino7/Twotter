import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.Schema
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.MigrationUtils

fun Application.connectToDatabase() {
//    println("jdbc:postgresql://db:5432/" + System.getenv("POSTGRES_DB"))


    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://db:5432/" + System.getenv("POSTGRES_DB")
        driverClassName = "org.postgresql.Driver"
        username = System.getenv("POSTGRES_USER")
        password = System.getenv("POSTGRES_PASSWORD")
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    //scuffed migration
    transaction {
        val schema = Schema("app") // my_schema is the schema name.
        SchemaUtils.setSchema(schema = schema)
        SchemaUtils.createSchema(schema)
        val res = MigrationUtils.statementsRequiredForDatabaseMigration(
            Users, Posts, Follows, Likes, Comments, Reposts
        )
        res.forEach(::exec)
        println(res)
    }
}
