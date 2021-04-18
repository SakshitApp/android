package com.sakshitapp.shared.repository

import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.Notification
import com.sakshitapp.shared.remote.CartAPI
import com.sakshitapp.shared.remote.ConfigAPI

class ConfigRepository {
    private val api = ConfigAPI()

    @Throws(Exception::class)
    suspend fun notifications(): List<Notification> {
        logMessage("CartRepository notifications")
        return api.notifications().data ?: emptyList()
    }
}