package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val uuid: String,
    val user: String? = null,
    val userName: String? = null,
    val title: String? = null,
    val image: String? = null,
    val summery: String? = null,
    val description: String? = null,
    val categories: List<Category> = emptyList(),
    val languages: List<Language> = emptyList(),
    val categoriesAll: List<Category> = emptyList(),
    val languagesAll: List<Language> = emptyList(),
    val price: Double = 0.0,
    val lessons: List<Lesson> = emptyList(),
    val likes: Long = 0,
    val subscriber: Long = 0,
    val review: List<Review> = emptyList(),
    val type: CourseType = CourseType.NO_INTRACTION,
    val state: CourseState = CourseState.DRAFT,
    val createdOn: Long? = null,
    val updatedOn: Long? = null,
) {
    var canEdit = false
}