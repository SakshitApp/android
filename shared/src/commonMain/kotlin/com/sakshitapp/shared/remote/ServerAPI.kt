package com.sakshitapp.shared.remote

import com.russhwolf.settings.Settings
import com.sakshitapp.shared.SharedFactory
import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.Response
import com.sakshitapp.shared.model.User
import io.ktor.client.HttpClient
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class ServerAPI {

    companion object {
        private const val URL = "https://powerful-badlands-26832.herokuapp.com"
        private const val AUTH = "Authorization"
    }

    private val shared: Settings by lazy {
        logMessage("ServerAPI initialize shared")
        SharedFactory.getInstance().preference()
    }

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
//        install(Logging){
//            logger = Logger.SIMPLE
//            level = LogLevel.ALL
//        }
        defaultRequest {
            header("Content-Type", ContentType.Application.Json.toString())
            header(AUTH, "Bearer ${shared.getString("token")}")
        }
    }


    @Throws(Exception::class)
    suspend fun getUser(): Response<User> {
        logMessage("ServerAPI getUser")
        return httpClient.get("${URL}/user")
    }

    @Throws(Exception::class)
    suspend fun updateUser(user: User): Response<User> {
        logMessage("ServerAPI updateUser $user")
        return httpClient.post("${URL}/user") {
            body = user
        }
    }

    @Throws(Exception::class)
    suspend fun updateUser(user: Map<*, *>): Response<User> {
        logMessage("ServerAPI updateUser $user")
        return httpClient.patch("${URL}/user") {
            body = user
        }
    }
}