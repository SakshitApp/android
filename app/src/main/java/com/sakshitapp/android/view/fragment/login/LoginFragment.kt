package com.sakshitapp.android.view.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentDashboardBinding
import com.sakshitapp.android.databinding.FragmentLoginBinding
import com.sakshitapp.android.view.MainActivity
import com.sakshitapp.android.view.fragment.dashboard.DashboardViewModel
import com.sakshitapp.android.viewmodel.ViewModelFactory

class LoginFragment: Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.forgotPassword.setOnClickListener {
            if (binding.tfEmail.editText?.text.isNullOrEmpty()) {
                binding.tfEmail.error = "Email is required"
            } else {
                binding.tfEmail.error = null
                binding.tfPassword.error = null
                viewModel.forgotPassword(binding.tfEmail.editText?.text.toString())
            }
        }
        binding.login.setOnClickListener {
            if (binding.tfEmail.editText?.text.isNullOrEmpty()) {
                binding.tfEmail.error = "Email is required"
                return@setOnClickListener
            } else {
                binding.tfEmail.error = null
            }
            if (binding.tfEmail.editText?.text.isNullOrEmpty()) {
                binding.tfEmail.error = "Password is required"
                return@setOnClickListener
            } else {
                binding.tfPassword.error = null
            }
            viewModel.login(binding.tfEmail.editText?.text.toString(), binding.tfPassword.editText?.text.toString())
        }
        binding.createNew.setOnClickListener {
            findNavController(this)
                .navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        observe()
        return binding.root
    }

    fun observe() {
        viewModel.getUsers().observe(viewLifecycleOwner, { user ->
            when {
                user.uuid.isEmpty() -> {
                    Snackbar.make(binding.root, "Failed", Snackbar.LENGTH_LONG).show()
                }
                user.role != null -> {
                    activity?.apply {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                else -> {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                    val controller = findNavController(this)
                    controller.navigate(R.id.action_loginFragment_to_roleFragment, null, navOptions)
                }
            }
        })
        viewModel.error().observe(viewLifecycleOwner, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
        viewModel.progress().observe(viewLifecycleOwner, {
            if (it) {
                activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}