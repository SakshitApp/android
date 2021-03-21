package com.sakshitapp.android.view.fragment.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentSignupBinding
import com.sakshitapp.android.view.MainActivity
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Role

class SignUpFragment: Fragment(){

    private val viewModel: SignUpViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentSignupBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.signUp.setOnClickListener {
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
            val role = when(binding.role.checkedButtonId) {
                R.id.teacher -> Role.USER_EXPERT
                else -> Role.USER_STUDENT
            }
            viewModel.signUp(binding.tfEmail.editText?.text.toString(), binding.tfPassword.editText?.text.toString(), role)
        }
        binding.login.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_signUpFragment_to_loginFragment)
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
                    val controller = NavHostFragment.findNavController(this)
                    controller.navigate(R.id.action_loginFragment_to_roleFragment, null, navOptions)
                }
            }
        })
        viewModel.error().observe(viewLifecycleOwner, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
        viewModel.progress().observe(viewLifecycleOwner, {
            if (it) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
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