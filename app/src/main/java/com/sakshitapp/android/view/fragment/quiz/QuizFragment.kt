package com.sakshitapp.android.view.fragment.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.QuizAdapter
import com.sakshitapp.android.databinding.FragmentQuizBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory

class QuizFragment: Fragment() {

    private val viewModel: QuizViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentQuizBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        binding.pager.isUserInputEnabled = false
        observe()
        binding.skip.setOnClickListener {
            findNavController()
                .navigate(R.id.action_quizFragment_to_congratulationFragment, Bundle().apply {
                    putAll(arguments)
                    putBoolean("hasPassed", true)
                })
        }
        return binding.root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { lesson ->
            binding.apply {
                val adapter = QuizAdapter(object : QuizAdapter.Callback {
                    override fun onSelect() {
                        pager.currentItem = pager.currentItem + 1
                    }

                    override fun onComplete(isPassed: Boolean) {
                        findNavController()
                            .navigate(R.id.action_quizFragment_to_congratulationFragment, Bundle().apply {
                                putAll(arguments)
                                putBoolean("hasPassed", isPassed)
                            })
                    }

                }, lesson.passingQuestion)
                pager.adapter = adapter
                adapter.submitList(lesson.question)
                if(lesson.passingQuestion == 0) {
                    skip.visibility = View.VISIBLE
                } else {
                    skip.visibility = View.GONE
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
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        val courseId = arguments?.getString("courseId")
        val lessonId = arguments?.getString("lessonId")
        viewModel.loadDrafts(courseId, lessonId)
    }
}