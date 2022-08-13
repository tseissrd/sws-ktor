package com.example.sws

import com.example.sws.data.DatabaseSpec
import com.example.sws.plugins.configureRouting
import com.example.sws.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.webjars.*
import org.flywaydb.core.Flyway

fun main() {
  embeddedServer(Netty, port = 8080, host = "") {
    module()
  }.start(wait = true)
}

fun Application.module() {
  val prodDsSpec = DatabaseSpec(
    url = environment.config.propertyOrNull("sws.database.production.url")?.getString()
      ?: "jdbc:postgresql://127.0.0.1:25432/sws",
    driver = environment.config.propertyOrNull("sws.database.production.driver")?.getString()
      ?: "org.postgresql.Driver",
    user = environment.config.propertyOrNull("sws.database.production.user")?.getString()
      ?: "admin",
    password = environment.config.propertyOrNull("sws.database.production.password")?.getString()
      ?: "admin"
  )

  val flyway = Flyway.configure()
    .dataSource(
      prodDsSpec.url,
      prodDsSpec.user,
      prodDsSpec.password
    ).locations("classpath:db/migration")
    .schemas("public")
    .cleanDisabled(false)
    .load()

  flyway.migrate()

  install(Webjars)
  configureRouting(prodDsSpec)
  configureSerialization()
}
