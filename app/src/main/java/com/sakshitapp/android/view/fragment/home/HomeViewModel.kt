package com.sakshitapp.android.view.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.EditCourse
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _draft: MutableLiveData<List<EditCourse>> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    init {
        loadDrafts()
    }

    fun getDrafts(): LiveData<List<EditCourse>> = _draft
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun loadDrafts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getCourses(forceRefresh)
            }.onSuccess {
                _progress.postValue(false)
                _draft.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun delete(course: EditCourse) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.delete(course)
            }.onSuccess {
                _progress.postValue(false)
                loadDrafts()
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

    fun changeState(course: EditCourse, state: CourseState) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.updateDraft(course.copy(state = state))
            }.onSuccess {
                _progress.postValue(false)
                loadDrafts()
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }
}