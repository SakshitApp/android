package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val subscribed: List<Subscription> = emptyList(),
    val courses: List<Course> = emptyList(),
)
