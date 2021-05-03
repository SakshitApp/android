package com.sakshitapp.shared.remote

import com.russhwolf.settings.Settings
import com.sakshitapp.shared.SharedFactory
import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.*
import io.ktor.client.HttpClient
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

open class ServerAPI {

    companion object {
        public const val URL = "https://powerful-badlands-26832.herokuapp.com"
        private const val AUTH = "Authorization"
    }

    private val shared: Settings by lazy {
        logMessage("ServerAPI initialize shared")
        SharedFactory.getInstance().preference()
    }

    protected val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
        install(Logging){
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 20 * 60 * 1000
            socketTimeoutMillis = 20 * 60 * 1000
            requestTimeoutMillis = 20 * 60 * 1000
        }
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

    @Throws(Exception::class)
    suspend fun getHomeCourses(): Response<Home> {
        logMessage("ServerAPI getHomeCourses")
        return httpClient.get("${URL}/course")
    }

    @Throws(Exception::class)
    suspend fun getDraftCourses(): Response<Home> {
        logMessage("ServerAPI getEditableCourses")
        return httpClient.get("${URL}/course/edit")
    }

    @Throws(Exception::class)
    suspend fun getNewDraftCourse(): Response<Course> {
        logMessage("ServerAPI getNewCourse")
        return httpClient.get("${URL}/course/edit/new")
    }

    @Throws(Exception::class)
    suspend fun getDraftCourse(courseId: String): Response<Course> {
        logMessage("ServerAPI getEditableCourse $courseId")
        return httpClient.get("${URL}/course/edit/$courseId")
    }

    @Throws(Exception::class)
    suspend fun getCourse(courseId: String): Response<Subscription> {
        logMessage("ServerAPI getCourse $courseId")
        return httpClient.get("${URL}/course/$courseId")
    }

    @Throws(Exception::class)
    suspend fun likeCourse(courseId: String): Response<Subscription> {
        logMessage("ServerAPI likeCourse $courseId")
        return httpClient.post("${URL}/course/like/$courseId")
    }

    @Throws(Exception::class)
    suspend fun lessonCompleted(courseId: String, data: Map<String, String>): Response<Subscription> {
        logMessage("ServerAPI lessonCompleted $courseId $data")
        return httpClient.post("${URL}/course/done/$courseId") {
            body = data
        }
    }

    @Throws(Exception::class)
    suspend fun reviewCourse(courseId: String, review: String): Response<Course> {
        logMessage("ServerAPI reviewCourse $courseId")
        return httpClient.post("${URL}/course/review/$courseId") {
            body = mapOf("review" to review)
        }
    }

    @Throws(Exception::class)
    suspend fun updateCourse(course: Course): Response<Course> {
        logMessage("ServerAPI updateCourse $course")
        return httpClient.post("${URL}/course/edit") {
            body = course
        }
    }

    @Throws(Exception::class)
    suspend fun deleteCourse(course: Course): Response<Course> {
        logMessage("ServerAPI updateCourse $course")
        return httpClient.delete("${URL}/course/edit") {
            body = course
        }
    }

    @Throws(Exception::class)
    suspend fun searchCourse(data: Map<String, String>): Response<List<Course>> {
        logMessage("ServerAPI searchCourse $data")
        return httpClient.post("${URL}/course/search") {
            body = data
        }
    }
}