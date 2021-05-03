package com.sakshitapp.shared

import com.russhwolf.settings.Settings
import com.sakshitapp.shared.cache.Database


expect class SharedFactory {

    companion object {
        fun getInstance(): SharedFactory
    }

    internal fun database(): Database
    internal fun preference(): Settings
}