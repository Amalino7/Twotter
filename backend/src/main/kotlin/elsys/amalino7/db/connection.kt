import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.Schema
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.MigrationUtils
import java.util.concurrent.TimeUnit

fun Application.connectToDatabase() {
    val dbConfig = environment.config.config("ktor.db")
    println("Connecting to database at ${dbConfig.property("url").getString()}")
    val config = HikariConfig().apply {
        jdbcUrl = dbConfig.property("url").getString()
        driverClassName = dbConfig.property("driver").getString()
        username = dbConfig.property("user").getString()
        password = dbConfig.property("password").getString()
        maximumPoolSize = dbConfig.property("maxPoolSize").getString().toInt()
        isAutoCommit = dbConfig.property("autoCommit").getString().toBoolean()
//        transactionIsolation = dbConfig.property("transactionIsolation").getString()
        maxLifetime = TimeUnit.MINUTES.toMillis(55) // Slightly less than any infrastructure timeout
        idleTimeout = TimeUnit.MINUTES.toMillis(10)
        connectionTestQuery = "SELECT 1"
        keepaliveTime = TimeUnit.MINUTES.toMillis(5)
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    //scuffed migration
    transaction {
        val schema = Schema("app") // my_schema is the schema name.
        SchemaUtils.setSchema(schema = schema)
        SchemaUtils.createSchema(schema)
        val res = MigrationUtils.statementsRequiredForDatabaseMigration(
            Users, Posts, Follows, Likes, Comments, Reposts, Images
        )
        res.forEach(::exec)
    }
}
