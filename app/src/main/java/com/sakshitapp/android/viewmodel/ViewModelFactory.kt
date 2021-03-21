package com.sakshitapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sakshitapp.android.FCMUserRepository
import com.sakshitapp.android.view.fragment.login.LoginViewModel
import com.sakshitapp.android.view.fragment.roles.RoleViewModel
import com.sakshitapp.android.view.fragment.signup.SignUpViewModel

class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == SplashViewModel::class.java
            || modelClass == LoginViewModel::class.java
            || modelClass == SignUpViewModel::class.java
            || modelClass == RoleViewModel::class.java) {
            return modelClass.getConstructor(FCMUserRepository::class.java)
                .newInstance(FCMUserRepository()) as T
        }
        return modelClass.newInstance() as T
    }
}