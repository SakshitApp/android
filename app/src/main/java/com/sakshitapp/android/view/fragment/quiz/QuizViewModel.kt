package com.sakshitapp.android.view.fragment.quiz

import android.content.Context
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.sakshitapp.shared.model.Lesson
import com.sakshitapp.shared.model.Subscription
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: CourseRepository) : ViewModel() {

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

    var course: Subscription? = null
    var lessonId: String? = null


    fun loadDrafts(courseId: String?, lessonId: String?) {
        this.lessonId = lessonId
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getCourse(courseId ?: "")
            }.onSuccess {
                _progress.postValue(false)
                course = it
                it.course?.lessons?.filter { it.uuid == lessonId }?.firstOrNull()?.let {
                    _data.postValue(it)
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
}