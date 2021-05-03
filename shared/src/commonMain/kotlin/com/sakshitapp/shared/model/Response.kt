package com.sakshitapp.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    @SerialName("code")
    val code: Int = 200,
    @SerialName("data")
    val data: T?,
    @SerialName("error")
    val error: String? = null
)
