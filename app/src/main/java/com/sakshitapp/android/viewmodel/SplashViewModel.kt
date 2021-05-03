package com.sakshitapp.android.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.android.FCMUserRepository
import com.sakshitapp.shared.model.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: FCMUserRepository): ViewModel() {

    private val _user: MutableLiveData<User> by lazy {
        MutableLiveData()
    }

    fun getUsers(): LiveData<User> = _user

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun error(): LiveData<String> = _error

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getUser(false)
            }.onSuccess {
                _user.postValue(it)
            }.onFailure {
                _error.postValue(it.localizedMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}