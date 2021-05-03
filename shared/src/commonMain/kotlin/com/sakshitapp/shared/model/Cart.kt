package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val uuid: String? = null,
    val user: String? = null,
    val course: Course? = null
)