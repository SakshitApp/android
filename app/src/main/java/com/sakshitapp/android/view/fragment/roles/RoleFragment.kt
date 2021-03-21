package com.sakshitapp.android.view.fragment.roles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentRoleBinding
import com.sakshitapp.android.databinding.FragmentSignupBinding
import com.sakshitapp.android.view.MainActivity
import com.sakshitapp.android.view.fragment.signup.SignUpViewModel
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Role

class RoleFragment: BottomSheetDialogFragment() {

    private val viewModel: RoleViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentRoleBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoleBinding.inflate(inflater, container, false)
        isCancelable = false
        binding.save.setOnClickListener {
            val role = when(binding.role.checkedButtonId) {
                R.id.teacher -> Role.USER_EXPERT
                else -> Role.USER_STUDENT
            }
            viewModel.selectRole(role)
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
                else -> {
                    activity?.apply {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
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