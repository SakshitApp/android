package com.sakshitapp.shared

import platform.Foundation.NSLog

actual fun logMessage(msg: String) {
    NSLog("Shared: ${msg}") // log the message using NSLog
}