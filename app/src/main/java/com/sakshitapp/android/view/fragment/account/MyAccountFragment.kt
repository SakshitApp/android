package com.sakshitapp.android.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.system.Os.accept
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentMyAccountBinding
import com.sakshitapp.android.view.SplashActivity
import com.sakshitapp.android.view.fragment.edit.course.ImageChooser
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Role


class MyAccountFragment : Fragment() {

    private val viewModel: MyAccountViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentMyAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        observe()
        binding.apply {
            uploadProfile.setOnClickListener {
                (activity as? ImageChooser)?.let {
                    it.selectImage {
                        viewModel.uploadImage(requireContext(), it)
                    }
                }
            }
            redeem.setOnClickListener {
                viewModel.redeem()
            }
            logout.setOnClickListener {
                viewModel.logout {
                    val intent = Intent(requireContext(), SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            }
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.apply {
                view.visibility = View.VISIBLE
                Glide.with(root.context)
                    .load(data.user?.photoURL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .into(profileImg)
                email.text = data.user?.email
                name.text = data.user?.name ?: "Unknown"
                editName.setOnClickListener {
                    showNameChangeDialog(data.user?.name)
                }
                notification.setOnCheckedChangeListener{buttonView, isChecked -> }
                notification.isChecked = data.isNotificationEnabled
                notification.setOnCheckedChangeListener { buttonView, isChecked ->
                    viewModel.setNotification(isChecked)
                }
                balance.text = getString(R.string.balance, data.redeem?: 0.0)
                subscribed.text = data.subscribedCourses?.toString()
                completed.text = data.completeCourses?.toString()
                active.text = data.activeCourses?.toString()
                inactive.text = data.inactiveCourses?.toString()
                draft.text = data.draftCourses?.toString()
                if (data.user?.role?.firstOrNull() == Role.USER_EXPERT) {
                    teacher.visibility = View.VISIBLE
                    student.visibility = View.GONE
                } else {
                    teacher.visibility = View.GONE
                    student.visibility = View.VISIBLE
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
        viewModel.load()
    }

    private fun showNameChangeDialog(name: String?) {
        val input = EditText(requireContext())
        input.setText(name)
        input.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_name)
            .setView(input)
            .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which -> }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                viewModel.setName(input.text.toString())
            }
            .show()
    }
}