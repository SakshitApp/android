package com.sakshitapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sakshitapp.android.FCMUserRepository

class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == SplashViewModel::class.java) {
            return modelClass.getConstructor(FCMUserRepository::class.java)
                .newInstance(FCMUserRepository()) as T
        }
        return modelClass.newInstance() as T
    }
}