package com.sakshitapp.shared.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.sakshitapp.shared.SharedFactory
import com.sakshitapp.shared.cache.Database
import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.Home
import com.sakshitapp.shared.model.Subscription
import com.sakshitapp.shared.remote.ServerAPI

class CourseRepository {

    private val api = ServerAPI()
    private val database: Database by lazy {
        SharedFactory.getInstance().database()
    }
    private val shared: Settings by lazy {
        SharedFactory.getInstance().preference()
    }

    val isTeacher : Boolean = shared.getBoolean("isTeacher", false)

    @Throws(Exception::class)
    suspend fun getCourses(forceReload: Boolean): Home {
        logMessage("CourseRepository getCourses $forceReload")
        val cached = database.getCourses()
        val subCached = database.getSubscribedCourses()
        val user = database.getUser()
        return if ((cached.isNotEmpty() || subCached.isNotEmpty()) && !forceReload) {
            logMessage("CourseRepository getCourses cached $cached $subCached")
            cached.forEach { it.canEdit = it.user == user?.uuid}
            subCached.forEach { it.course?.canEdit = it.course?.user == user?.uuid }
            Home(getSubs(cached, subCached), sortCourses(getCors(cached, subCached, false)))
        } else {
            api.getDraftCourses().let { response ->
                logMessage("CourseRepository getCourses response $response")
                return response.data?.let { data ->
                    database.clearCourse()
                    database.createCourses(data.courses)
                    database.createSCourses(data.subscribed)
                    data.courses.forEach {it.canEdit = it.user == user?.uuid }
                    data.subscribed.forEach { it.course?.canEdit = it.course?.user == user?.uuid }
                    Home(getSubs(data.courses, data.subscribed), sortCourses(getCors(data.courses, data.subscribed, false)))
                } ?: Home(emptyList(), emptyList())
            }
        }
    }

    fun sortCourses(course: List<Course>): List<Course> {
        return arrayListOf<Course>().apply {
            addAll(course.filter { it.state == CourseState.ACTIVE })
            addAll(course.filter { it.state == CourseState.INACTIVE })
            addAll(course.filter { it.state == CourseState.DRAFT })
        }
    }

    @Throws(Exception::class)
    suspend fun getHome(forceReload: Boolean): Home {
        logMessage("CourseRepository getHome $forceReload")
        val cached = database.getCourses()
        val subCached = database.getSubscribedCourses()
        val user = database.getUser()
        return if ((cached.isNotEmpty() || subCached.isNotEmpty()) && !forceReload) {
            logMessage("CourseRepository getHome cached $cached $subCached")
            cached.forEach { it.canEdit = it.user == user?.uuid}
            subCached.forEach { it.course?.canEdit = it.course?.user == user?.uuid }
            Home(getSubs(cached, subCached), getCors(cached, subCached))
        } else {
            api.getHomeCourses().let { response ->
                logMessage("CourseRepository getHome response $response")
                return response.data?.let { data ->
                    database.clearCourse()
                    database.createCourses(data.courses)
                    database.createSCourses(data.subscribed)
                    data.courses.forEach { it.canEdit = it.user == user?.uuid}
                    data.subscribed.forEach { it.course?.canEdit = it.course?.user == user?.uuid }
                    Home(getSubs(data.courses, data.subscribed), getCors(data.courses, data.subscribed))
                } ?: Home(emptyList(), emptyList())
            }
        }
    }

    fun getCors(course: List<Course>, subs: List<Subscription>, showSubs:Boolean = true): List<Course> {
        val hashMap = hashMapOf<String, Course>()
        course.forEach {
            hashMap[it.uuid] = it
        }
        subs.forEach {
            if (it.transactionId != null) {
                hashMap.remove(it.courseId)
            } else if (it.course != null && showSubs) {
                hashMap[it.courseId] = it.course
            }
        }
        return hashMap.values.toList()
    }

    fun getSubs(course: List<Course>, subs: List<Subscription>): List<Subscription> {
        return arrayListOf<Subscription>().apply {
            addAll(subs.filter { it.transactionId != null })
        }
    }

    @Throws(Exception::class)
    suspend fun getDraftCourse(id: String? = null): Course {
        logMessage("CourseRepository getDraftCourse $id")
        val cache = id?.let { database.getCourse(it) }
        if (cache != null) {
            logMessage("CourseRepository getDraftCourse cache $cache")
            val cat = database.getCategories()
            val lan = database.getLanguages()
            return cache.copy(categoriesAll = cat, languagesAll = lan)
        }
        (if (id != null) api.getDraftCourse(id) else api.getNewDraftCourse())
            .let { response ->
                logMessage("CourseRepository getDraftCourse response $response")
                return response.data?.let { data ->
                    database.createCourse(data)
                    data
                } ?: throw Exception("Not Found")
            }
    }

    @Throws(Exception::class)
    suspend fun getCourse(id: String): Subscription {
        logMessage("CourseRepository getCourse $id")
        val cache = id?.let { database.getSubscriptionCourse(it) }
        val user = database.getUser()
        if (cache?.course != null) {
            logMessage("CourseRepository getCourse cache $cache")
            cache.course?.canEdit = cache.course?.user == user?.uuid
            return cache
        }
        api.getCourse(id)
            .let { response ->
                logMessage("CourseRepository getCourse response $response")
                return response.data?.let { data ->
                    database.deleteSubscriptionCourse(data)
                    database.createCourse(data)
                    data.course?.canEdit = data.course?.user == user?.uuid
                    data
                } ?: throw Exception("Not Found")
            }
    }

    @Throws(Exception::class)
    suspend fun likeCourse(id: String): Subscription {
        logMessage("CourseRepository likeCourse $id")
        val user = database.getUser()
        api.likeCourse(id)
            .let { response ->
                logMessage("CourseRepository likeCourse response $response")
                return response.data?.let { data ->
                    database.deleteSubscriptionCourse(data)
                    database.createCourse(data)
                    data.course?.canEdit = data.course?.user == user?.uuid
                    data
                } ?: throw Exception("Not Found")
            }
    }

    @Throws(Exception::class)
    suspend fun lessonCompleted(id: String, lesson: String): Subscription {
        logMessage("CourseRepository lessonCompleted $id $lesson")
        val user = database.getUser()
        api.lessonCompleted(id, mapOf("lesson" to lesson))
            .let { response ->
                logMessage("CourseRepository lessonCompleted response $response")
                return response.data?.let { data ->
                    database.deleteSubscriptionCourse(data)
                    database.createCourse(data)
                    data?.course?.canEdit = data?.course?.user == user?.uuid
                    data
                } ?: throw Exception("Not Found")
            }
    }

    @Throws(Exception::class)
    suspend fun reviewCourse(id: String, review: String): Subscription {
        logMessage("CourseRepository reviewCourse $id")
        val user = database.getUser()
        api.reviewCourse(id, review)
            .let { response ->
                logMessage("CourseRepository reviewCourse response $response")
                return response.data?.let { data ->
                    database.getSubscriptionCourse(id)?.let {
                        val sub = it.copy(course = data)
                        database.deleteSubscriptionCourse(it)
                        database.createCourse(sub)
                        sub?.course?.canEdit = sub?.course?.user == user?.uuid
                        sub
                    }
                } ?: throw Exception("Not Found")
            }
    }

    @Throws(Exception::class)
    suspend fun updateDraft(draft: Course): Course {
        logMessage("CourseRepository updateDraft $draft")
        val user = database.getUser()
        api.updateCourse(draft).let { response ->
            logMessage("CourseRepository updateDraft response $response")
            return response.data?.let { data ->
                database.createCourse(data)
                data?.canEdit = data?.user == user?.uuid
                data
            } ?: throw Exception("Not Found")
        }
    }

    @Throws(Exception::class)
    suspend fun delete(draft: Course) {
        logMessage("CourseRepository delete $draft")
        api.deleteCourse(draft).let { response ->
            logMessage("CourseRepository delete response $response")
            response.data?.let { data ->
                database.deleteCourse(data)
            } ?: throw Exception("Not Found")
        }
    }

    @Throws(Exception::class)
    suspend fun search(text: String): List<Course> {
        logMessage("CourseRepository search $text")
        val user = database.getUser()
        api.searchCourse(mapOf("text" to text)).let { response ->
            logMessage("CourseRepository search response $response")
            response.data?.forEach { it.canEdit = it.user == user?.uuid }
            return response.data ?: emptyList()
        }
    }
}