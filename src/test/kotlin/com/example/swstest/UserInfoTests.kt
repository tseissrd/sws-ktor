package com.example.swstest

import com.example.swstest.controller.userInfoRouter
import com.example.swstest.data.DatabaseSpec
import com.example.swstest.entity.UserInfo
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.flywaydb.core.Flyway
import org.junit.Test
import kotlin.test.assertEquals

class UserInfoTests {

	private companion object {
		val infoMockInput: UserInfo
		val infoMockResult: UserInfo

		init {
			val firstName = "fn"
			val lastName = "ln"
			val email = "test@yandex.ru"

			infoMockInput = UserInfo(
				null,
				firstName,
				lastName,
				null,
				email,
				null
			)

			infoMockResult = UserInfo(
				1,
				firstName,
				lastName,
				null,
				email,
				null
			)
		}
	}

	private fun initDatasource(appBuilder: ApplicationTestBuilder) {
		val testDsSpec = DatabaseSpec(
			url = "jdbc:postgresql://127.0.0.1:25432/sws-test",
			driver = "org.postgresql.Driver",
			user = "admin",
			password = "admin"
		)

		val flyway = Flyway.configure()
			.dataSource(
				testDsSpec.url,
				testDsSpec.user,
				testDsSpec.password
			).locations("classpath:db/migration")
			.schemas("public")
			.cleanDisabled(false)
			.load()

		flyway.clean()
		flyway.migrate()

		appBuilder.routing {
			route("/test") {
				userInfoRouter(testDsSpec)
			}
		}
	}

	@Test
	fun getUserInfo() = testApplication {
		initDatasource(this);

		val client = createClient {
			install(ContentNegotiation) {
				json()
			}
		}

		val postResponse = client.post("/test/userinfo") {
			contentType(ContentType.Application.Json)
			setBody(infoMockInput)
		}

		assertEquals(infoMockResult, postResponse.body())
		assertEquals(HttpStatusCode.OK, postResponse.status)

		val response = client.get(
			"/test/userinfo/"
				+ postResponse.body<UserInfo>()
					.id
		)

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals(postResponse.body<UserInfo>(), response.body())
	}

	@Test
	fun postUserInfo() = testApplication {
		initDatasource(this);

		val client = createClient {
			install(ContentNegotiation) {
				json()
			}
		}

		val response = client.post("/test/userinfo") {
			contentType(ContentType.Application.Json)
			setBody(infoMockInput)
		}

		assertEquals(infoMockResult, response.body())
		assertEquals(HttpStatusCode.OK, response.status)

	}

}
