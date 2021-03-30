package com.sakshitapp.android.view.fragment.edit.course

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.sakshitapp.shared.model.*
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EditCourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _draft: MutableLiveData<EditCourse> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getDraft(): LiveData<EditCourse> = _draft
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun uploadImage(path: Uri?) {
        _draft.value?.let { course ->
            if (path == null || path.path == null ) {
                _error.postValue("No File Selected")
                return
            }
            _progress.postValue(true)
            val storageRef = FirebaseStorage.getInstance().reference
            val ext = path.path!!.substring(path.path!!.lastIndexOf("."));
            val imagesRef = storageRef.child("images/${course.uuid}${ext}")

            val uploadTask = imagesRef.putFile(path)
            uploadTask.addOnFailureListener {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imagesRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            save(course.copy(image = task.result.toString()))
                        } else {
                            _progress.postValue(false)
                            _error.postValue(task.exception?.localizedMessage)
                        }
                    }
                } else {
                    _progress.postValue(false)
                    _error.postValue(task.exception?.localizedMessage)
                }
            }
        }
    }

    fun loadDrafts(id: String?) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getDraftCourse(id)
            }.onSuccess {
                _progress.postValue(false)
                _draft.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun save(course: EditCourse) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.updateDraft(course)
            }.onSuccess {
                _progress.postValue(false)
                _draft.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun save(title: String?, summery: String?, description: String?, categories: List<String>, languages: List<String>, price: Double?) {
        _draft.value?.let { course ->
            val c = course.copy(
                title = title,
                summery = summery,
                description = description,
                categories = categories.map { category ->  course.categoriesAll.filter { it.name ==  category}.firstOrNull() ?: Category(name = category) },
                languages = languages.map { language ->  course.languagesAll.filter { it.name ==  language}.firstOrNull() ?: Language(name = language) },
                price = price ?: 0.0
            )
            save(c)
        }
    }

    fun setState(isActive: Boolean) {
        _draft.value?.let { course ->
            val c = course.copy(
                state = if(isActive) CourseState.ACTIVE else CourseState.INACTIVE
            )
            save(c)
        }
    }

    fun removeLesson(lesson: Lesson) {
        _draft.value?.let { course ->
            save(course.copy(lessons = course.lessons.filter { it.uuid != lesson.uuid }))
        }
    }
}