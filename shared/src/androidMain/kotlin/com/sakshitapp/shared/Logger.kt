package com.sakshitapp.shared

import android.util.Log

actual fun logMessage(msg: String) {
    Log.d("Shared", msg)
}