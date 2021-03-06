package com.sakshitapp.android

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.sakshitapp.shared.model.Role
import com.sakshitapp.shared.model.User
import com.sakshitapp.shared.repository.UserRepository
import kotlinx.coroutines.GlobalScope
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
                        }.onSuccess { user ->
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w(
                                            TAG,
                                            "Fetching FCM registration token failed",
                                            task.exception
                                        )
                                        coroutine.resume(user ?: User(""))
                                        return@OnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    val token = task.result
                                    if (token != null) {
                                        GlobalScope.launch {
                                            kotlin.runCatching {
                                                super.sendFCMToken(token)
                                            }.onSuccess {
                                                coroutine.resume(it ?: User(""))
                                            }.onFailure {
                                                coroutine.resumeWithException(it)
                                            }
                                        }
                                    } else {
                                        coroutine.resume(user ?: User(""))
                                    }
                                })
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

    suspend fun forgotPassword(email: String): Boolean = suspendCoroutine { coroutine ->
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    coroutine.resume(true)
                } else {
                    coroutine.resumeWithException(it.exception ?: Exception("Failed"))
                }
            }.addOnCanceledListener {
                coroutine.resumeWithException(Exception("Cancelled"))
            }
    }

    suspend fun create(
        email: String,
        password: String,
        role: Role
    ): User = suspendCoroutine { coroutine ->
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful && it.result?.user != null) {
                    it.result?.user?.getIdToken(true)?.addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result?.token != null) {
                            task.result?.token?.let { setToken(it) }
                            GlobalScope.launch {
                                kotlin.runCatching {
                                    super.setRole(role)
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
                    coroutine.resumeWithException(it.exception ?: Exception("Failed"))
                }
            }
    }

    suspend fun login(email: String, password: String): User = suspendCoroutine { coroutine ->
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    GlobalScope.launch {
                        kotlin.runCatching {
                            this@FCMUserRepository.getUser(true)
                        }.onSuccess {
                            if (it.uuid.isNotEmpty()) {
                                coroutine.resume(it)
                            } else {
                                coroutine.resumeWithException(Exception("Failed"))
                            }
                        }.onFailure {
                            coroutine.resumeWithException(it)
                        }
                    }
                } else {
                    coroutine.resumeWithException(it.exception ?: Exception("Failed"))
                }
            }
    }

    suspend fun logout() {
        FirebaseAuth.getInstance().signOut()
        super.signOut()
    }

    companion object {
        private const val TAG = "FCMUserRepository"
    }

}