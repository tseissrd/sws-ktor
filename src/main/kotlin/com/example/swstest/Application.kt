package com.example.swstest

import com.example.swstest.data.DatabaseSpec
import com.example.swstest.plugins.configureRouting
import com.example.swstest.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

val prodDsSpec = DatabaseSpec(
  url = "jdbc:postgresql://127.0.0.1:25432/sws",
  driver = "org.postgresql.Driver",
  user = "admin",
  password = "admin"
)

fun main() {
  embeddedServer(Netty, port = 8080, host = "") {
    configureRouting(prodDsSpec)
    configureSerialization()
  }.start(wait = true)
}

fun Application.module() {
  configureRouting(prodDsSpec)
  configureSerialization()
}
