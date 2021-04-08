package com.sakshitapp.android.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ActivityLauncherBinding
import com.sakshitapp.android.viewmodel.SplashViewModel
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Role


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private val viewModel: SplashViewModel by viewModels {
        ViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observe()

    }

    private fun observe() {

        viewModel.getUsers().observe(this, { user ->
            when {
                user.uuid.isEmpty() -> {
                    binding.navHostFragmentActivityUser.visibility = View.VISIBLE
                }
                user.role != null && user.role!!.isNotEmpty() -> {
                    if (user.role!![0] == Role.USER_STUDENT) {
                        startActivity(Intent(this, MainStudentActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                else -> {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                    val controller = findNavController(R.id.nav_host_fragment_activity_user)
                    controller.navigate(R.id.action_loginFragment_to_roleFragment, null, navOptions)
                    binding.navHostFragmentActivityUser.visibility = View.VISIBLE
                }
            }
        })
        viewModel.error().observe(this, { error ->
            binding.navHostFragmentActivityUser.visibility = View.VISIBLE
        })
    }
}