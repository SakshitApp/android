package com.sakshitapp.android.view.fragment.account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.sakshitapp.android.FCMUserRepository
import com.sakshitapp.android.util.getFilename
import com.sakshitapp.shared.model.Config
import com.sakshitapp.shared.repository.ConfigRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MyAccountViewModel(private val repository: ConfigRepository, private val userRepository: FCMUserRepository) : ViewModel() {

    private val _data: MutableLiveData<Config> by lazy {
        MutableLiveData()
    }

    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    private val _progress: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun getData(): LiveData<Config> = _data
    fun error(): LiveData<String> = _error
    fun progress(): LiveData<Boolean> = _progress

    fun load() {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                repository.getConfig()
            }.onSuccess {
                _progress.postValue(false)
                _data.postValue(it)
            }.onFailure {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }
        }
    }

    fun uploadImage(context: Context, path: Uri?) {
        _data.value?.let { config ->
            if (path == null || path.path == null ) {
                _error.postValue("No File Selected")
                return
            }
            _progress.postValue(true)
            val storageRef = FirebaseStorage.getInstance().reference
            val filename = path.getFilename(context)
            val ext = filename!!.substring(filename!!.lastIndexOf("."));
            val imagesRef = storageRef.child("images/${config.userId}${ext}")

            val uploadTask = imagesRef.putFile(path)
            uploadTask.addOnFailureListener {
                _progress.postValue(false)
                _error.postValue(it.localizedMessage)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imagesRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModelScope.launch {
                                kotlin.runCatching {
                                    config.user?.copy(photoURL = task.result.toString())?.let { it1 -> userRepository.updateUser(it1) }
                                }.onSuccess {
                                    _progress.postValue(false)
                                    _data.postValue(config.copy(user = it))
                                }.onFailure {
                                    _progress.postValue(false)
                                    _error.postValue(it.localizedMessage)
                                }
                            }
                        } else {
                            _progress.postValue(false)
                            _error.postValue(task.exception?.localizedMessage)
                        }
                    }
                } else {
                    _progress.postValue(false)
                    _error.postValue(task.exception?.localizedMessage)
                }
            }
        }
    }

    fun setName(name: String?) {
        if (name.isNullOrEmpty()) return
        _data.value?.let { config ->
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    config.user?.copy(name = name)?.let { it1 -> userRepository.updateUser(it1) }
                }.onSuccess {
                    _progress.postValue(false)
                    _data.postValue(config.copy(user = it))
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun setNotification(value: Boolean) {
        _data.value?.let { config ->
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.saveConfig(config.copy(isNotificationEnabled = value))
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

    fun redeem() {
        _data.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    _progress.postValue(true)
                    repository.redeem()
                }.onSuccess {
                    _progress.postValue(false)
                    load()
                }.onFailure {
                    _progress.postValue(false)
                    _error.postValue(it.localizedMessage)
                }
            }
        }
    }

    fun logout(value: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                _progress.postValue(true)
                userRepository.logout()
            }.onSuccess {
                _progress.postValue(false)
                value()
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