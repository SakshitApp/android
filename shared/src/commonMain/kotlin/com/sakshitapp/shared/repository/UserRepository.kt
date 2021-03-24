package com.sakshitapp.shared.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.sakshitapp.shared.SharedFactory
import com.sakshitapp.shared.cache.Database
import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.Role
import com.sakshitapp.shared.model.User
import com.sakshitapp.shared.remote.ServerAPI

open class UserRepository {
    private val api = ServerAPI()
    private val database: Database by lazy {
        SharedFactory.getInstance().database()
    }
    private val shared: Settings by lazy {
        SharedFactory.getInstance().preference()
    }

    open fun setToken(token: String) {
        shared["token"] = token
    }

    @Throws(Exception::class)
    open suspend fun getUser(forceReload: Boolean): User? {
        logMessage("UserRepository getUser $forceReload")
        val cachedUser = database.getUser()
        return if (cachedUser != null && !forceReload) {
            logMessage("UserRepository getUser cached $cachedUser")
            cachedUser
        } else {
            api.getUser().let {
                logMessage("UserRepository getUser response $it")
                database.clearDatabase()
                database.createUser(it.data)
                it.data
            }
        }
    }

    @Throws(Exception::class)
    open suspend fun updateUser(user: User): User? {
        logMessage("UserRepository updateUser $user")
        return api.updateUser(user).let {
            logMessage("UserRepository updateUser response $it")
            database.clearUser()
            database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun setRole(role: Role): User? {
        logMessage("UserRepository setRole $role")
        return api.updateUser(mapOf("role" to listOf(role.name))).let {
            logMessage("UserRepository setRole response $it")
            database.clearUser()
            database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun sendFCMToken(token: String): User? {
        logMessage("UserRepository sendFCMToken $token")
        return api.updateUser(mapOf("fcmToken" to token)).let {
            logMessage("UserRepository sendFCMToken response $it")
            database.clearUser()
            database.createUser(it.data)
            it.data
        }
    }

    @Throws(Exception::class)
    open suspend fun delete() {
        logMessage("UserRepository delete")
        return api.updateUser(mapOf("isActive" to false)).let {
            logMessage("UserRepository delete response $it")
            database.clearUser()
        }
    }
}