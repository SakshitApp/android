package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val userId: String,
    val user: User? = null,
    val isNotificationEnabled: Boolean = false,
    val redeem: Double? = null,
    val subscribedCourses: Int? = null,
    val completeCourses: Int? = null,
    val activeCourses: Int? = null,
    val inactiveCourses: Int? = null,
    val draftCourses: Int? = null
)