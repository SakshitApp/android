package com.sakshitapp.android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.sakshitapp.android.databinding.ActivityLauncherBinding
import com.sakshitapp.android.viewmodel.SplashViewModel
import com.sakshitapp.android.viewmodel.ViewModelFactory

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
                    Toast.makeText(this, "Login", Toast.LENGTH_LONG).show()
                }
                user.role != null -> {
                    Toast.makeText(this, "Goto Home", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this, "Select Roles", Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.error().observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        })
    }
}