package com.sakshitapp.android.view.fragment.congratulation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentCongratulationsBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class CongratulationFragment : Fragment() {

    private val viewModel: CongratulationViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentCongratulationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val hasPassed get() = arguments?.getBoolean("hasPassed") ?: false
    private val isLast get() = arguments?.getBoolean("isLast") ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCongratulationsBinding.inflate(inflater, container, false)
        binding.retry.setOnClickListener {
            findNavController()
                .popBackStack(R.id.lessonFragment, hasPassed)
        }
        binding.retry.text = if (hasPassed) getString(R.string.close) else getString(R.string.retry)
        binding.retry.visibility = View.GONE
        observe()
        return binding.root
    }

    private fun observe() {
        viewModel.error().observe(viewLifecycleOwner, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
        viewModel.progress().observe(viewLifecycleOwner, {
            if (it) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        val courseId = arguments?.getString("courseId")
        val lessonId = arguments?.getString("lessonId")
        if (hasPassed) {
            viewModel.finish(courseId, lessonId) {
                binding.retry.visibility = View.VISIBLE
                binding.retry.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
                binding.status.text =
                    if (isLast) getString(R.string.course_complete_status) else getString(R.string.lesson_complete_status)
                binding.status.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_green_dark
                    )
                )
                binding.viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12))
                    .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                    .streamFor(300, 5000L)
            }
        } else {
            binding.retry.visibility = View.VISIBLE
            binding.status.text = getString(R.string.lesson_failed_status)
            binding.status.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            binding.retry.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark)
        }
    }
}