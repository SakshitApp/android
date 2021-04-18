package com.sakshitapp.android.view.fragment.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.shared.model.Notification
import com.sakshitapp.shared.repository.ConfigRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repository: ConfigRepository) : ViewModel() {

    private val _data: MutableLiveData<List<Notification>> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<List<Notification>> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun notifications() {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.notifications()
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