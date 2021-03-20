package com.sakshitapp.android

import android.app.Application
import com.sakshitapp.shared.SharedSDK
import com.sakshitapp.shared.cache.DatabaseDriverFactory

class SakshitApp: Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        SharedSDK.setDatabase(DatabaseDriverFactory(this))
    }

    companion object {
        private lateinit var INSTANCE: SakshitApp
        fun getInstance(): SakshitApp {
            return INSTANCE
        }
    }
}