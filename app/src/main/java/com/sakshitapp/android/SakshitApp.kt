package com.sakshitapp.android

import android.app.Application
import com.sakshitapp.shared.SharedFactory

class SakshitApp: Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        SharedFactory.initialize(this)
    }

    companion object {
        private lateinit var INSTANCE: SakshitApp
        fun getInstance(): SakshitApp {
            return INSTANCE
        }
    }
}