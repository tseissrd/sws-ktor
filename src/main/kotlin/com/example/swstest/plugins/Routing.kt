package com.example.swstest.plugins

import com.example.swstest.controller.userInfoRouter
import com.example.swstest.data.DatabaseSpec
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting(databaseSpec: DatabaseSpec) {
  routing {
    static("/") {
      staticBasePackage = "static"
      resources(".")
      defaultResource("swagger.html")
    }
    route("/v1") {
      userInfoRouter(this, databaseSpec)
    }
  }
}