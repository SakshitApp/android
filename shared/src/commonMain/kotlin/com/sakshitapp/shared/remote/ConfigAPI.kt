package com.sakshitapp.shared.remote

import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.*
import io.ktor.client.request.*

class ConfigAPI: ServerAPI() {

    @Throws(Exception::class)
    suspend fun notifications(): Response<List<Notification>> {
        logMessage("ServerAPI notification")
        return httpClient.get("${URL}/notification")
    }

    @Throws(Exception::class)
    suspend fun getConfig(): Response<Config> {
        logMessage("ServerAPI getConfig")
        return httpClient.get("${URL}/config")
    }

    @Throws(Exception::class)
    suspend fun saveConfig(config: Config): Response<Config> {
        logMessage("ServerAPI saveConfig $config")
        return httpClient.post("${URL}/config") {
            body = config
        }
    }

    @Throws(Exception::class)
    suspend fun redeem(): Response<String> {
        logMessage("ServerAPI redeem")
        return httpClient.get("${URL}/redeem")
    }
}