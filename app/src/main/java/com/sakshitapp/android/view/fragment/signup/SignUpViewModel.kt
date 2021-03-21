package com.sakshitapp.android.view.fragment.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakshitapp.android.FCMUserRepository
import com.sakshitapp.shared.model.Role
import com.sakshitapp.shared.model.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: FCMUserRepository) : ViewModel() {

    private val _user: MutableLiveData<User> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getUsers(): LiveData<User> = _user
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun signUp(email: String, password: String, role: Role) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.create(email, password, role)
            }.onSuccess {
                _progress.postValue(false)
                _user.postValue(it)
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