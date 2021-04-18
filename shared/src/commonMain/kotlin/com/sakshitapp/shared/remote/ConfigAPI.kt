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
}