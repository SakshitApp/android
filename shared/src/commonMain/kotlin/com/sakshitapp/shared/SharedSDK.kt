package com.sakshitapp.shared

import com.sakshitapp.shared.cache.Database
import com.sakshitapp.shared.cache.DatabaseDriverFactory
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SharedSDK {

    internal lateinit var database: Database
    internal var token: String? = null

    fun setDatabase(databaseDriverFactory: DatabaseDriverFactory) {
        database = Database(databaseDriverFactory)
    }
}