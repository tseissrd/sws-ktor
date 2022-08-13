package com.example.sws.controller

import com.example.sws.data.DatabaseSpec
import com.example.sws.entity.UserInfo
import com.example.sws.service.UserInfoService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

fun userInfoRouter(parent: Route, databaseSpec: DatabaseSpec) {
  val userInfoService = UserInfoService(databaseSpec)

  parent.route("/userinfo") {
    UserInfoRouter.getUserInfo(this, userInfoService)
    UserInfoRouter.postUserInfo(this, userInfoService)
  }
}

@Path("/v1/userinfo")
object UserInfoRouter {

  @Path("/{id}")
  @GET
  @Operation(
    summary = "Get user info for ID",
    tags = ["UserInfo"],
    description = "Returns UserInfo object for the ID provided in request path",
    parameters = [
      Parameter(
        name = "id",
        `in` = ParameterIn.PATH,
        schema = Schema(
          implementation = Long::class
        )
      )
    ],
    requestBody = RequestBody(
      content = [
        Content(
          schema = Schema(
            implementation = Void::class
          )
        )
      ]
    ),
    responses = [
      ApiResponse(
        description = "User info",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = UserInfo::class)
          )
        ],
        responseCode = "200"
      ),
      ApiResponse(
        description = "Missing id",
        content = [
          Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(implementation = String::class),
            examples = [
              ExampleObject("Missing id")
            ]
          )
        ],
        responseCode = "400",
      ),
      ApiResponse(
        description = "User info not found",
        content = [
          Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(implementation = String::class),
            examples = [
              ExampleObject("Could not find info for ID 1")
            ]
          )
        ],
        responseCode = "404",
      )
    ]
  )
  fun getUserInfo(parent: Route, userInfoService: UserInfoService) {
    parent.get("/{id}") {
      val id = call.parameters["id"] ?: return@get call.respondText(
        "Missing id",
        status = HttpStatusCode.BadRequest
      )

      try {
        call.respond(
          userInfoService.readInfo(
            id.toLong()
          )
        )
      } catch (ex: SQLException) {
        Logger.getLogger(UserInfoRouter::class::qualifiedName.name).log(Level.INFO, null, ex)
        call.respondText(
          "Could not find info for ID $id",
          status = HttpStatusCode.NotFound
        )
      }
    }
  }

  @Operation(
    summary = "Post user info",
    tags = ["UserInfo"],
    description = "Saves user info and returns populated UserInfo object",
    requestBody = RequestBody(
      description = "User info",
      content = [
        Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = Schema(
            implementation = UserInfo::class
          )
        )
      ],
      required = true
    ),
    responses = [
      ApiResponse(
        description = "Populated user info",
        content = [
          Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = UserInfo::class)
          )
        ],
        responseCode = "200"
      ),
      ApiResponse(
        description = "Malformed UserInfo passed",
        content = [
          Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(implementation = String::class),
            examples = [
              ExampleObject("Illegal input")
            ]
          )
        ],
        responseCode = "400",
      )
    ]
  )
  @Path("/")
  @POST
  fun postUserInfo(parent: Route, userInfoService: UserInfoService) {
    parent.post {
      try {
        val info = call.receive<UserInfo>()

        call.respond(
          userInfoService.createInfo(info)
        )
      } catch (ex: BadRequestException) {
        call.respondText(
          ex.localizedMessage,
          status = HttpStatusCode.BadRequest
        )
      }
    }
  }
}
