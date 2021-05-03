package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val uuid: String? = null,
    val question: String? = null,
    val answers: List<String> = emptyList(),
    val correct: String = ""
)
