package com.sakshitapp.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class CourseState {
    DRAFT,
    ACTIVE,
    INACTIVE,
    SUBSCRIBED,
    DELETED
}