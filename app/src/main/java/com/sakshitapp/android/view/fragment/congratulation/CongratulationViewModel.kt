package com.sakshitapp.android.view.fragment.congratulation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CongratulationViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun finish(courseId: String?, lessonId: String?, value: () -> Unit) {
        courseId?.let {
            lessonId?.let {
                viewModelScope.launch {
                    kotlin.runCatching {
                        _progress.postValue(true)
                        repository.lessonCompleted(courseId, lessonId)
                    }.onSuccess {
                        _progress.postValue(false)
                        value()
                    }.onFailure {
                        _progress.postValue(false)
                        _error.postValue(it.localizedMessage)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}