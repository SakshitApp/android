package com.sakshitapp.android.view.fragment.lesson

import android.content.Context
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.Lesson
import com.sakshitapp.shared.model.Question
import com.sakshitapp.shared.model.Subscription
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class LessonViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<Lesson> by lazy {
        MutableLiveData()
    }

    private val _link: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<Lesson> = _data
    fun getLink(): LiveData<String> = _link
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    var course: Subscription? = null
    var lessonId: String? = null


    fun loadDrafts(context: Context, courseId: String?, lessonId: String?) {
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
                    getLink(context, it.content)
                }
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun getLink(context: Context, url: String?) {
        url?.let {
            if (url.contains("youtube") || url.contains("youtu.be/")) {
                object : YouTubeExtractor(context) {
                    override fun onExtractionComplete(
                        ytFiles: SparseArray<YtFile>,
                        vMeta: VideoMeta
                    ) {
                        if (ytFiles != null) {
                            when {
                                ytFiles[22] != null -> {
                                    _link.postValue(ytFiles[22].url)
                                }
                                ytFiles[35] != null -> {
                                    _link.postValue(ytFiles[35].url)
                                }
                                ytFiles[18] != null -> {
                                    _link.postValue(ytFiles[18].url)
                                }
                                ytFiles[34] != null -> {
                                    _link.postValue(ytFiles[34].url)
                                }
                                ytFiles[17] != null -> {
                                    _link.postValue(ytFiles[17].url)
                                }
                                ytFiles[36] != null -> {
                                    _link.postValue(ytFiles[36].url)
                                }
                            }
                        }
                    }
                }.extract(url, true, true)
            } else {
                _link.postValue(url)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}