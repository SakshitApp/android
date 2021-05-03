package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val uuid: String? = null,
    val user: String,
    val courseId: String,
    val course: Course? = null,
    val isLiked: Boolean = false,
    val progress: List<String> = emptyList(),
    val transactionId: String? = null
)