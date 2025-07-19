package elsys.amalino7

import elsys.amalino7.di.configureFrameworks
import elsys.amalino7.infrastructure.db.connectToDatabase
import elsys.amalino7.plugins.*
import elsys.amalino7.security.configureSecurity
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectToDatabase()
    configureHTTP()
    configureAdministration()
    configureSecurity()
    configureFrameworks()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
