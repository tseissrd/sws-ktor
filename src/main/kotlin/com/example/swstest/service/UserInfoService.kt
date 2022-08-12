package com.example.swstest.service

import com.example.swstest.data.DatabaseSpec
import com.example.swstest.entity.UserInfo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

class UserInfoService(val databaseSpec: DatabaseSpec) {

  fun readInfo(id: Long): UserInfo {
    Database.connect(
      databaseSpec.url,
      databaseSpec.driver,
      databaseSpec.user,
      databaseSpec.password
    )

    var results: ResultSet? = null;

    transaction {
      val statement = TransactionManager.current()
        .connection
        .prepareStatement("SELECT * FROM user_info WHERE id = ?", false)

      statement.fillParameters(
        listOf(
          Pair(LongColumnType(), id)
        )
      )

      results = statement.executeQuery()
    }

    results!!.next()

    return UserInfo(
      results!!.getLong("id"),
      results!!.getString("first_name"),
      results!!.getString("last_name"),
      results!!.getString("middle_name"),
      results!!.getString("email"),
      results!!.getString("phone")
    )
  }

  fun createInfo(info: UserInfo): UserInfo {
    Database.connect(
      databaseSpec.url,
      databaseSpec.driver,
      databaseSpec.user,
      databaseSpec.password
    )

    var results: ResultSet? = null;

    transaction {
      val statement = TransactionManager.current()
        .connection
        .prepareStatement(
          "INSERT INTO user_info(first_name, last_name, middle_name, email, phone)" +
            "VALUES (?, ?, ?, ?, ?)" +
            " RETURNING id, first_name, last_name, middle_name, email, phone",
          false
        )

      statement.fillParameters(
        listOf(
          Pair(VarCharColumnType(), info.firstName),
          Pair(VarCharColumnType(), info.lastName),
          Pair(VarCharColumnType(), info.middleName),
          Pair(VarCharColumnType(), info.email),
          Pair(VarCharColumnType(), info.phone)
        )
      )

      results = statement.executeQuery()
    }

    results!!.next()

    return UserInfo(
      results!!.getLong("id"),
      results!!.getString("first_name"),
      results!!.getString("last_name"),
      results!!.getString("middle_name"),
      results!!.getString("email"),
      results!!.getString("phone")
    )
  }
}