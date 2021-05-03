package com.sakshitapp.shared

import android.content.Context
import android.content.SharedPreferences
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.sakshitapp.shared.cache.AppDatabase
import com.sakshitapp.shared.cache.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import java.util.concurrent.atomic.AtomicReference

actual class SharedFactory constructor(
    private val context: Context
) {
    actual companion object {
        private val INSTANCE = AtomicReference<SharedFactory>()
        fun initialize(context: Context) {
            val shared = SharedFactory(context)
            INSTANCE.compareAndSet(null, shared)
        }

        actual fun getInstance(): SharedFactory {
            return INSTANCE.get()
                ?: throw UninitializedPropertyAccessException("SharedSDK is not initialised yet")
        }
    }

    internal actual fun database(): Database {
        val drive = AndroidSqliteDriver(AppDatabase.Schema, context, "shared.db")
        return Database(drive)
    }
    internal actual fun preference(): Settings {
        val delegate: SharedPreferences =
            context.getSharedPreferences("shared", Context.MODE_PRIVATE)
        return AndroidSettings(delegate)
    }
}