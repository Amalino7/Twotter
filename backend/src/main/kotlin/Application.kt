package com.example

import connectToDatabase
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectToDatabase()
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
//    configureAdministration()
    configureRouting()
}
