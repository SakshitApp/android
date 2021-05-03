package com.sakshitapp.android.view.fragment.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.Cart
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.RazorPayOrder
import com.sakshitapp.shared.model.RazorPayVerify
import com.sakshitapp.shared.repository.CartRepository
import com.sakshitapp.shared.repository.CourseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _data: MutableLiveData<List<Course>> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<List<Course>> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    private var job: Job? =null

    fun search(text: String?) {
        if (text.isNullOrEmpty()) return
        job?.cancel()
        job = viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.search(text)
            }.onSuccess {
                _progress.postValue(false)
                _data.postValue(it)
            }.onFailure {
                if (it !is CancellationException) {
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