package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val uuid: String? = null,
    val title: String? = null,
    val image: String? = null,
    val description: String? = null,
    val content: String? = null,
    val review: List<Review> = emptyList(),
    val question: List<Question> = emptyList(),
    val passingQuestion: Int = 0,
    val likes: Long = 0
)
