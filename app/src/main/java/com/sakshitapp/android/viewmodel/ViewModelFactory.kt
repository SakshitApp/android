package com.sakshitapp.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sakshitapp.android.FCMUserRepository
import com.sakshitapp.android.view.fragment.cart.CartViewModel
import com.sakshitapp.android.view.fragment.course.CourseViewModel
import com.sakshitapp.android.view.fragment.edit.course.EditCourseViewModel
import com.sakshitapp.android.view.fragment.edit.lesson.EditLessonViewModel
import com.sakshitapp.android.view.fragment.edit.question.EditQuestionViewModel
import com.sakshitapp.android.view.fragment.home.HomeViewModel
import com.sakshitapp.android.view.fragment.home.StudentHomeViewModel
import com.sakshitapp.android.view.fragment.login.LoginViewModel
import com.sakshitapp.android.view.fragment.roles.RoleViewModel
import com.sakshitapp.android.view.fragment.signup.SignUpViewModel
import com.sakshitapp.shared.repository.CartRepository
import com.sakshitapp.shared.repository.CourseRepository

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == SplashViewModel::class.java
            || modelClass == LoginViewModel::class.java
            || modelClass == SignUpViewModel::class.java
            || modelClass == RoleViewModel::class.java
        ) {
            return modelClass.getConstructor(FCMUserRepository::class.java)
                .newInstance(FCMUserRepository()) as T
        } else if (modelClass == HomeViewModel::class.java
            || modelClass == EditCourseViewModel::class.java
            || modelClass == EditLessonViewModel::class.java
            || modelClass == EditQuestionViewModel::class.java
            || modelClass == StudentHomeViewModel::class.java
        ) {
            return modelClass.getConstructor(CourseRepository::class.java)
                .newInstance(CourseRepository()) as T
        } else if (modelClass == CourseViewModel::class.java) {
            return modelClass.getConstructor(
                CourseRepository::class.java,
                CartRepository::class.java
            )
                .newInstance(CourseRepository(), CartRepository()) as T
        } else if (modelClass == CartViewModel::class.java) {
            return modelClass.getConstructor(CartRepository::class.java)
                .newInstance(CartRepository()) as T
        }
        return modelClass.newInstance() as T
    }
}