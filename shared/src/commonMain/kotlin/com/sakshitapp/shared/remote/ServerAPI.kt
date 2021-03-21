package com.sakshitapp.shared.remote

import com.sakshitapp.shared.SharedSDK
import com.sakshitapp.shared.model.Response
import com.sakshitapp.shared.model.User
import io.ktor.client.HttpClient
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class ServerAPI {

    companion object {
        private const val URL = "https://powerful-badlands-26832.herokuapp.com"
        private const val AUTH = "Authorization"
    }

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
        install(Logging)
        defaultRequest {
            header("Content-Type", ContentType.Application.Json.toString())
            header(AUTH, "Bearer ${SharedSDK.token}")
        }
    }

    suspend fun getUser(): Response<User> {
        return httpClient.get("${URL}/user")
    }

    suspend fun updateUser(user: User): Response<User> {
        return httpClient.post("${URL}/user") {
            body = user
        }
    }

    suspend fun updateUser(user: Map<*, *>): Response<User> {
        return httpClient.patch("${URL}/user") {
            body = user
        }
    }
}