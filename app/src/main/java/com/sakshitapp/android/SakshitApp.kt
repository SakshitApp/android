package com.sakshitapp.android

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.sakshitapp.shared.SharedFactory

class SakshitApp: Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        SharedFactory.initialize(this)
        FirebaseMessaging.getInstance().subscribeToTopic("SakshitApp")
    }

    companion object {
        private lateinit var INSTANCE: SakshitApp
        fun getInstance(): SakshitApp {
            return INSTANCE
        }
    }
}