package elsys.amalino7

import connectToDatabase
import elsys.amalino7.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectToDatabase()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureMonitoring()
//    configureAdministration()
    configureRouting()
}
