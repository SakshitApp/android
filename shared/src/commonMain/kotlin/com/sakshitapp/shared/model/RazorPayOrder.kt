package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class RazorPayOrder(
    val id: String,
    val amount: Long,
    val currency: String,
    val receipt: String
)
