package com.sakshitapp.shared.repository

import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.Config
import com.sakshitapp.shared.model.Notification
import com.sakshitapp.shared.model.Response
import com.sakshitapp.shared.remote.CartAPI
import com.sakshitapp.shared.remote.ConfigAPI
import com.sakshitapp.shared.remote.ServerAPI
import io.ktor.client.request.*

class ConfigRepository {
    private val api = ConfigAPI()

    @Throws(Exception::class)
    suspend fun notifications(): List<Notification> {
        logMessage("ConfigRepository notifications")
        return api.notifications().data ?: emptyList()
    }

    @Throws(Exception::class)
    suspend fun getConfig(): Config {
        logMessage("ConfigRepository getConfig")
        return api.getConfig().data ?: throw Exception("Error")
    }

    @Throws(Exception::class)
    suspend fun saveConfig(config: Config): Config {
        logMessage("ConfigRepository saveConfig $config")
        return api.saveConfig(config).data ?: throw Exception("Error")
    }

    @Throws(Exception::class)
    suspend fun redeem() {
        logMessage("ConfigRepository redeem")
        api.redeem().data ?: throw Exception("Error")
    }
}