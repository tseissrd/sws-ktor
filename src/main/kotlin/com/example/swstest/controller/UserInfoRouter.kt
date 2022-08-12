package com.example.swstest.controller

import com.example.swstest.data.DatabaseSpec
import com.example.swstest.entity.UserInfo
import com.example.swstest.service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userInfoRouter(databaseSpec: DatabaseSpec) {
  val userInfoService = UserInfoService(databaseSpec)

  route("/userinfo") {
    post {
      val info = call.receive<UserInfo>()

      call.respond(
        userInfoService.createInfo(info)
      )
    }
    
    get("/{id}") {
      val id = call.parameters["id"] ?: return@get call.respondText(
        "Missing id",
        status = HttpStatusCode.BadRequest
      )

      call.respond(
        userInfoService.readInfo(
          id.toLong()
        )
      )
    }
  }
}
