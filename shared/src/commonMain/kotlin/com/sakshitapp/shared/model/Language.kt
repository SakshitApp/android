package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val uuid: String? = null,
    val name: String? = null
)