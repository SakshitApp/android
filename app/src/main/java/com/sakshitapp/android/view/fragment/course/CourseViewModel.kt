package com.sakshitapp.android.view.fragment.course

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

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<Subscription> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<Subscription> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun load(id: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getCourse(id)
            }.onSuccess {
                _progress.postValue(false)
                _data.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun like() {
        _data.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.likeCourse(it.courseId)
                }.onSuccess {
                    _progress.postValue(false)
                    _data.postValue(it)
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun review(review: String?) {
        if (review == null) {
            _error.postValue("Review cannot be empty")
            return
        }
        _data.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.reviewCourse(it.courseId, review)
                }.onSuccess {
                    _progress.postValue(false)
                    _data.postValue(it)
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}