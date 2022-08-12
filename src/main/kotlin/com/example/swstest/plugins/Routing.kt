package com.example.swstest.plugins

import com.example.swstest.controller.userInfoRouter
import com.example.swstest.data.DatabaseSpec
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(databaseSpec: DatabaseSpec) {
    routing {
        userInfoRouter(databaseSpec)
    }
}