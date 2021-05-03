package com.sakshitapp.shared.cache

import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Role
import com.sakshitapp.shared.model.Subscription
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.json.Json

internal class Database(driver: SqlDriver){
    private val database = AppDatabase(driver)
    private val dbQuery = database.appDatabaseQueries
    private val json = Json { ignoreUnknownKeys = true }

    internal fun clearDatabase() {
        dbQuery.transaction {
            clearUser()
            clearCourse()
            clearCategory()
            clearLanguage()
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

    internal fun clearCourse() {
        dbQuery.removeAllCourse()
    }

    internal fun deleteCourse(course: Course?) {
        course?.let { dbQuery.removeCourse(it.uuid) }
    }

    internal fun deleteSubscriptionCourse(course: Subscription?) {
        course?.let { dbQuery.removeCourse(course.courseId) }
    }

    internal fun getCourse(id: String): Course? {
        return dbQuery.selectCourseForOtherState(id, CourseState.SUBSCRIBED.name, ::mapCourse).executeAsOneOrNull()
    }

    internal fun getCourses(): List<Course> {
        return dbQuery.selectCoursesForOtherState(CourseState.SUBSCRIBED.name, ::mapCourse).executeAsList()
    }

    private fun mapCourse(
        uuid: String,
        jsonString: String?,
        state: String?
    ): com.sakshitapp.shared.model.Course {
        return json.decodeFromString(com.sakshitapp.shared.model.Course.serializer(), jsonString?: "")
    }

    internal fun createCourses(course: List<Course>?) {
        dbQuery.transaction {
            course?.forEach {
                insertCourse(it)
            }
        }
    }

    internal fun createSCourses(course: List<Subscription>?) {
        dbQuery.transaction {
            course?.forEach {
                insertCourse(it)
            }
        }
    }
    internal fun createCourse(course: Course?) {
        dbQuery.transaction {
            course?.let {
                deleteCourse(course)
                clearCategory()
                clearLanguage()
                insertCourse(it.copy(
                    categoriesAll = emptyList(),
                    languagesAll = emptyList()
                ))
                createCategory(course.categoriesAll)
                createLanguage(course.languagesAll)
            }
        }
    }

    private fun insertCourse(course: Course) {
        dbQuery.insertCourse(
            uuid = course.uuid,
            json = json.encodeToString(com.sakshitapp.shared.model.Course.serializer(), course),
            state = course.state.name
        )
    }

    internal fun getSubscribedCourses(): List<Subscription> {
        return dbQuery.selectCoursesForState(CourseState.SUBSCRIBED.name, ::mapSubscribedCourse).executeAsList()
    }

    private fun mapSubscribedCourse(
        uuid: String,
        jsonString: String?,
        state: String?
    ): com.sakshitapp.shared.model.Subscription {
        return json.decodeFromString(com.sakshitapp.shared.model.Subscription.serializer(), jsonString?: "")
    }

    internal fun getSubscriptionCourse(id: String): Subscription? {
        return dbQuery.selectCourseForState(id, CourseState.SUBSCRIBED.name, ::mapSubscribedCourse).executeAsOneOrNull()
    }

    internal fun createCourse(course: Subscription?) {
        dbQuery.transaction {
            course?.let {
                insertCourse(it)
            }
        }
    }

    private fun insertCourse(course: Subscription) {
        dbQuery.insertCourse(
            uuid = course.courseId,
            json = json.encodeToString(com.sakshitapp.shared.model.Subscription.serializer(), course),
            state = CourseState.SUBSCRIBED.name
        )
    }

    internal fun clearCategory() {
        dbQuery.removeAllCategory()
    }

    internal fun getCategories(): List<com.sakshitapp.shared.model.Category> {
        return dbQuery.selectAllCategory(::mapCategory).executeAsList()
    }

    private fun mapCategory(
        uuid: String,
        name: String?
    ): com.sakshitapp.shared.model.Category {
        return com.sakshitapp.shared.model.Category(uuid, name)
    }

    internal fun createCategory(course: List<com.sakshitapp.shared.model.Category>?) {
        dbQuery.transaction {
            course?.forEach {
                insertCategory(it)
            }
        }
    }

    private fun insertCategory(category: com.sakshitapp.shared.model.Category) {
        dbQuery.insertCategory(
            uuid = category.uuid ?:"",
            name = category.name ?:""
        )
    }

    internal fun clearLanguage() {
        dbQuery.removeAllLanguage()
    }

    internal fun getLanguages(): List<com.sakshitapp.shared.model.Language> {
        return dbQuery.selectAllLanguage(::mapLanguage).executeAsList()
    }

    private fun mapLanguage(
        uuid: String,
        name: String?
    ): com.sakshitapp.shared.model.Language {
        return com.sakshitapp.shared.model.Language(uuid, name)
    }

    internal fun createLanguage(course: List<com.sakshitapp.shared.model.Language>?) {
        dbQuery.transaction {
            course?.forEach {
                insertLanguage(it)
            }
        }
    }

    private fun insertLanguage(language: com.sakshitapp.shared.model.Language) {
        dbQuery.insertCategory(
            uuid = language.uuid ?:"",
            name = language.name ?:""
        )
    }
}