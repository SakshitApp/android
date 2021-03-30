package com.sakshitapp.android.view.fragment.edit.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.QuestionStatusAdapter
import com.sakshitapp.android.databinding.FragmentEditLessonBinding
import com.sakshitapp.android.databinding.FragmentEditQuestionBinding
import com.sakshitapp.android.view.fragment.edit.course.ImageChooser
import com.sakshitapp.android.view.fragment.edit.lesson.EditLessonViewModel
import com.sakshitapp.android.view.fragment.edit.lesson.FileChooser
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Question

class EditQuestionFragment : Fragment() {

    private val viewModel: EditQuestionViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentEditQuestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditQuestionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        observe()
        binding.save.setOnClickListener {
            viewModel.save(
                binding.question.editText?.text?.toString(),
                binding.ans1.editText?.text?.toString(),
                binding.ans2.editText?.text?.toString(),
                binding.ans3.editText?.text?.toString(),
                binding.ans4.editText?.text?.toString(),
                binding.correctAnswer.selectedItem.toString().toInt()
            )
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { q ->
            binding.apply {
                question.editText?.setText(q.question)
                if (q.answers.isNotEmpty()) {
                    ans1.editText?.setText(q.answers[0])
                }
                if (q.answers.size > 1) {
                    ans2.editText?.setText(q.answers[1])
                }
                if (q.answers.size > 2) {
                    ans3.editText?.setText(q.answers[2])
                }
                if (q.answers.size > 3) {
                    ans4.editText?.setText(q.answers[3])
                }
                val ad: ArrayAdapter<String> = ArrayAdapter<String>(
                    root.context,
                    android.R.layout.simple_spinner_item,
                    (1..4).map { it.toString() }
                )
                ad.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                correctAnswer.adapter = ad
                q.answers.forEachIndexed { index, ans ->
                    if (q.correct == ans) {
                        correctAnswer.setSelection(index)
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
        val courseId = arguments?.getString("courseId")
        val lessonId = arguments?.getString("lessonId")
        val questionId = arguments?.getString("questionId")
        viewModel.loadDrafts(courseId, lessonId, questionId)
    }
}