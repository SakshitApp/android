package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val uuid: String? = null,
    val title: String? = null,
    val image: String? = null,
    val content: String? = null,
    val redirects: String? = null,
    val redirectType: RedirectType? = null
)