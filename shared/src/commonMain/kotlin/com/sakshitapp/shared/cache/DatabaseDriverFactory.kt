package com.sakshitapp.shared.cache

import com.squareup.sqldelight.db.SqlDriver

expect open class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}