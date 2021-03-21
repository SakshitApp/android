package com.sakshitapp.shared.cache

import com.sakshitapp.shared.model.Role

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            clearUser()
        }
    }

    internal fun clearUser() {
        dbQuery.removeAllUser()
    }

    internal fun getUser(): com.sakshitapp.shared.model.User? {
        return dbQuery.selectUser(::mapUser).executeAsOneOrNull()
    }

    private fun mapUser(
        uuid: String,
        name: String?,
        photoUrl: String?,
        email: String?,
        phoneNumber: String?,
        role: String?
    ): com.sakshitapp.shared.model.User {
        return com.sakshitapp.shared.model.User(
            uuid = uuid,
            name = name,
            photoURL = photoUrl,
            email = email,
            phoneNumber = phoneNumber,
            role = role?.let { listOf(Role.valueOf(it)) }
        )
    }

    internal fun createUser(user: com.sakshitapp.shared.model.User?) {
        dbQuery.transaction {
            user?.let {
                insertRocket(it)
            }
        }
    }

    private fun insertRocket(user: com.sakshitapp.shared.model.User) {
        dbQuery.insertUser(
            uuid = user.uuid,
            name = user.name,
            photoURL = user.photoURL,
            email = user.email,
            phoneNumber = user.phoneNumber,
            role = if (!user.role.isNullOrEmpty()) user.role.first().name else null
        )
    }
}