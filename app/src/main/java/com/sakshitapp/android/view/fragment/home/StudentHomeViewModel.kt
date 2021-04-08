package com.sakshitapp.android.view.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Home
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StudentHomeViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<Home> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    init {
        load()
    }

    fun getData(): LiveData<Home> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun load(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getHome(forceRefresh)
            }.onSuccess {
                _progress.postValue(false)
                _data.postValue(it)
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