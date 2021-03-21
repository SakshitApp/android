package com.sakshitapp.shared.repository

import com.sakshitapp.shared.SharedSDK
import com.sakshitapp.shared.model.Role
import com.sakshitapp.shared.model.User
import com.sakshitapp.shared.remote.ServerAPI

open class UserRepository {
    private val api = ServerAPI()

    open fun setToken(token: String) {
        SharedSDK.token = token
    }

    @Throws(Exception::class)
    open suspend fun getUser(forceReload: Boolean): User? {
        if (SharedSDK.token == null) return null
        val cachedUser = SharedSDK.database.getUser()
        return if (cachedUser != null && !forceReload) {
            cachedUser
        } else {
            api.getUser().let {
                SharedSDK.database.clearDatabase()
                SharedSDK.database.createUser(it.data)
                it.data
            }
        }
    }

    @Throws(Exception::class)
    open suspend fun updateUser(user: User): User? {
        if (SharedSDK.token == null) return null
        return api.updateUser(user).let {
            SharedSDK.database.clearUser()
            SharedSDK.database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun setRole(role: Role): User? {
        if (SharedSDK.token == null) return null
        return api.updateUser(mapOf("role" to listOf(role.name))).let {
            SharedSDK.database.clearUser()
            SharedSDK.database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun sendFCMToken(token: String): User? {
        if (SharedSDK.token == null) return null
        return api.updateUser(mapOf("fcmToken" to token)).let {
            SharedSDK.database.clearUser()
            SharedSDK.database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun delete() {
        if (SharedSDK.token == null) return
        return api.updateUser(mapOf("isActive" to false)).let {
            SharedSDK.database.clearUser()
        }
    }
}