import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.Schema
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.MigrationUtils

fun Application.connectToDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/" + dotenv().get("POSTGRES_DB")
        driverClassName = "org.postgresql.Driver"
        username = dotenv().get("POSTGRES_USER")
        password = dotenv().get("POSTGRES_PASSWORD")
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    //scuffed migration
    val statements = transaction {
        val schema = Schema("app") // my_schema is the schema name.
        SchemaUtils.setSchema(schema = schema)
        SchemaUtils.createSchema(schema)
        val res = MigrationUtils.statementsRequiredForDatabaseMigration(
            Users, Posts, Follows
        );
        res.forEach(::exec)
        println(res);
    }
}
