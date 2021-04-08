package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionState {
    STARTED,
    FAILED,
    COMPLETED
}