package com.example.sws.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
  val id: Long? = null,
  val firstName: String,
  val lastName: String,
  val middleName: String? = null,
  val email: String? = null,
  val phone: String? = null
)
