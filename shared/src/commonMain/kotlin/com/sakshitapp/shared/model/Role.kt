package com.sakshitapp.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    @SerialName("USER_EXPERT")
    USER_EXPERT,
    @SerialName("USER_STUDENT")
    USER_STUDENT
}