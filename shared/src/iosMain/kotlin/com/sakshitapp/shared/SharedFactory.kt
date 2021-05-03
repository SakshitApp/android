package com.sakshitapp.shared

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import com.sakshitapp.shared.cache.AppDatabase
import com.sakshitapp.shared.cache.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import platform.Foundation.NSUserDefaults
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

actual class SharedFactory {
    actual companion object {
        private val INSTANCE = AtomicReference<SharedFactory?>(null)
        fun initialize() {
            val sharedSDK = SharedFactory()
            INSTANCE.compareAndSet(null, sharedSDK.freeze())
        }

        actual fun getInstance(): SharedFactory {
            return INSTANCE.value
                ?: throw UninitializedPropertyAccessException("SharedSDK is not initialised yet")
        }
    }

    internal actual fun database(): Database {
        val driver = NativeSqliteDriver(AppDatabase.Schema, "shared.db")
        return Database(driver)
    }
    internal actual fun preference(): Settings {
        val delegate = NSUserDefaults.standardUserDefaults()
        return AppleSettings(delegate)
    }
}