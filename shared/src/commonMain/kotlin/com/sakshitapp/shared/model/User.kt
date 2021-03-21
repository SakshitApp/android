package com.sakshitapp.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("uid")
    val uuid: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("photoURL")
    val photoURL: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("role")
    val role: List<Role>? = null
)
