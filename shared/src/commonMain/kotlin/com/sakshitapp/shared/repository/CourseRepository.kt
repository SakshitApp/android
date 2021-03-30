package com.sakshitapp.shared.repository

import com.russhwolf.settings.Settings
import com.sakshitapp.shared.SharedFactory
import com.sakshitapp.shared.cache.Database
import com.sakshitapp.shared.logMessage
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.EditCourse
import com.sakshitapp.shared.remote.ServerAPI

class CourseRepository {

    private val api = ServerAPI()
    private val database: Database by lazy {
        SharedFactory.getInstance().database()
    }
    private val shared: Settings by lazy {
        SharedFactory.getInstance().preference()
    }

    @Throws(Exception::class)
    suspend fun getCourses(forceReload: Boolean): List<EditCourse> {
        logMessage("CourseRepository getCourses $forceReload")
        val cached = database.getCourses()
        return if (cached != null && !forceReload) {
            logMessage("CourseRepository getCourses cached $cached")
            sortCourses(cached)
        } else {
            api.getDraftCourses().let { response ->
                logMessage("CourseRepository getCourses response $response")
                return response.data?.let { data ->
                    database.clearCourse()
                    database.createCourses(data)
                    sortCourses(data)
                } ?: emptyList()
            }
        }
    }

    fun sortCourses(course: List<EditCourse>): List<EditCourse> {
        return arrayListOf<EditCourse>().apply {
            addAll(course.filter { it.state == CourseState.ACTIVE })
            addAll(course.filter { it.state == CourseState.INACTIVE })
            addAll(course.filter { it.state == CourseState.DRAFT })
        }
    }

    @Throws(Exception::class)
    suspend fun getDraftCourse(id: String? = null): EditCourse {
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
    suspend fun updateDraft(draft: EditCourse): EditCourse {
        logMessage("CourseRepository updateDraft $draft")
        api.updateCourse(draft).let { response ->
            logMessage("CourseRepository updateDraft response $response")
            return response.data?.let { data ->
                database.createCourse(data)
                data
            } ?: throw Exception("Not Found")
        }
    }

    @Throws(Exception::class)
    suspend fun delete(draft: EditCourse) {
        logMessage("CourseRepository delete $draft")
        api.deleteCourse(draft).let { response ->
            logMessage("CourseRepository delete response $response")
            response.data?.let { data ->
                database.deleteCourse(data)
            } ?: throw Exception("Not Found")
        }
    }
}