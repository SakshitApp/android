package com.sakshitapp.android

import com.google.firebase.auth.FirebaseAuth
import com.sakshitapp.shared.model.User
import com.sakshitapp.shared.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FCMUserRepository : UserRepository() {

    override suspend fun getUser(forceReload: Boolean): User = suspendCoroutine { coroutine ->
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful && task.result?.token != null) {
                    task.result?.token?.let { setToken(it) }
                    GlobalScope.launch {
                        kotlin.runCatching {
                            super.getUser(false)
                        }.onSuccess {
                            coroutine.resume(it ?: User(""))
                        }.onFailure {
                            coroutine.resumeWithException(it)
                        }
                    }
                } else {
                    coroutine.resumeWithException(
                        task.exception ?: java.lang.Exception("Not Found")
                    )
                }
            }
        } else {
            coroutine.resume(User(""))
        }
    }

}