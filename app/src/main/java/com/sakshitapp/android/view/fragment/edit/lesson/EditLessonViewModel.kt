package com.sakshitapp.android.view.fragment.edit.lesson

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.sakshitapp.android.util.getFilename
import com.sakshitapp.shared.model.*
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EditLessonViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<Lesson> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<Lesson> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    var course: Course? = null
    var lessonId: String? = null

    fun uploadImage(context: Context, path: Uri?) {
        _data.value?.let { lesson ->
            if (path == null || path.path == null ) {
                _error.postValue("No File Selected")
                return
            }
            _progress.postValue(true)
            val storageRef = FirebaseStorage.getInstance().reference
            val filename = path.getFilename(context)
            val ext = filename!!.substring(filename!!.lastIndexOf("."));
            val imagesRef = storageRef.child("images/${lesson.uuid}${ext}")

            val uploadTask = imagesRef.putFile(path)
            uploadTask.addOnFailureListener {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imagesRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            save(lesson.copy(image = task.result.toString()))
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

    fun uploadFile(path: Uri?) {
        _data.value?.let { lesson ->
            if (path == null || path.path == null ) {
                _error.postValue("No File Selected")
                return
            }
            _progress.postValue(true)
            val storageRef = FirebaseStorage.getInstance().reference
            val ext = path.path!!.substring(path.path!!.lastIndexOf("."));
            val contentRef = storageRef.child("content/${lesson.uuid}${ext}")

            val uploadTask = contentRef.putFile(path)
            uploadTask.addOnFailureListener {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    contentRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            save(lesson.copy(content = task.result.toString()))
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


    fun loadDrafts(courseId: String?, lessonId: String?) {
        this.lessonId = lessonId
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getDraftCourse(courseId)
            }.onSuccess {
                _progress.postValue(false)
                course = it
                if (lessonId != null) {
                    _data.postValue(it.lessons.filter { it.uuid == lessonId }.firstOrNull())
                } else {
                    val lessons = arrayListOf<Lesson>()
                    lessons.addAll(it.lessons)
                    lessons.add(Lesson())
                    save(it.copy(lessons = lessons))
                }
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun save(course: Course) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.updateDraft(course)
            }.onSuccess {
                _progress.postValue(false)
                this@EditLessonViewModel.course = it
                if (lessonId != null) {
                    _data.postValue(it.lessons.filter { it.uuid == lessonId }.firstOrNull())
                } else {
                    lessonId = it.lessons.lastOrNull()?.uuid
                    _data.postValue(it.lessons.lastOrNull())
                }
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

    fun save(newLesson: Lesson) {
        course?.let {
            val lessons = arrayListOf<Lesson>()
            it.lessons.forEach { lesson ->
                if (lesson.uuid == newLesson.uuid) {
                    lessons.add(newLesson)
                } else {
                    lessons.add(lesson)
                }
            }
            save(it.copy(lessons = lessons))
        }
    }

    fun save(title: String?, description: String?, youtubeUrl: String?, passingQuestion: Int) {
        var ytThumb: String? = null
        val yt = if (youtubeUrl?.contains("youtube") == true) {
            ytThumb = "https://img.youtube.com/vi/${youtubeUrl.split("?v=", "&")[1]}/maxresdefault.jpg"
            youtubeUrl
        } else if (youtubeUrl?.contains("youtu.be/") == true) {
            ytThumb = "https://img.youtube.com/vi/${youtubeUrl.split("youtu.be/")[1]}/maxresdefault.jpg"
            youtubeUrl
        } else null

        _data.value?.let {
            save(it.copy(
                title = title,
                description = description,
                content = yt ?: it.content,
                image = if (yt != it.content) ytThumb ?: it.image else it.image,
                passingQuestion = passingQuestion
            ))
        }
    }

    fun deleteQuestion(question: Question) {
        _data.value?.let {
            save(it.copy(question = it.question.filter { it.uuid != question.uuid }))
        }
    }
}