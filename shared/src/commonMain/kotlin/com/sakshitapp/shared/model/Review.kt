package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val uuid: String? = null,
    val user: String? = null,
    val review: String? = null,
    val reply: List<Review> = emptyList()
)
