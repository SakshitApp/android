package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val uuid: String? = null,
    val user: String? = null,
    val courses: List<Course> = emptyList(),
    val state: TransactionState = TransactionState.STARTED,
    val createdOn: Long? = null,
    val updatedOn: Long? = null
)