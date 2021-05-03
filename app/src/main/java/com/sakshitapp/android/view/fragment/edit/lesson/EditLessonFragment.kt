package com.sakshitapp.android.view.fragment.edit.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.QuestionStatusAdapter
import com.sakshitapp.android.databinding.FragmentEditLessonBinding
import com.sakshitapp.android.view.fragment.edit.course.ImageChooser
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Question

class EditLessonFragment : Fragment() {

    private val viewModel: EditLessonViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentEditLessonBinding? = null
    private lateinit var questionAdapter: QuestionStatusAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLessonBinding.inflate(inflater, container, false)
        val root: View = binding.root
        questionAdapter = QuestionStatusAdapter(object: QuestionStatusAdapter.Callback{
            override fun onNew() = findNavController()
                .navigate(R.id.action_editLessonFragment_to_editQuestionFragment, bundleOf("courseId" to viewModel.course?.uuid, "lessonId" to viewModel.lessonId))

            override fun onClick(question: Question) = findNavController()
                .navigate(R.id.action_editLessonFragment_to_editQuestionFragment, bundleOf("courseId" to viewModel.course?.uuid, "lessonId" to viewModel.lessonId, "questionId" to question.uuid))

            override fun onDelete(question: Question) = viewModel.deleteQuestion(question)

        })
        binding.questionRv.adapter = questionAdapter
        observe()
        binding.changeImage.setOnClickListener {
            (activity as? ImageChooser)?.let {
                it.selectImage {
                    viewModel.uploadImage(requireContext(), it)
                }
            }
        }
        binding.save.setOnClickListener {
            viewModel.save(
                binding.title.editText?.text?.toString(),
                binding.description.editText?.text?.toString(),
                binding.youtubeUrl.editText?.text?.toString(),
                binding.passingQuestions.selectedItem.toString().toInt()
            )
        }
        binding.upload.setOnClickListener {
            (activity as? FileChooser)?.let {
                it.select {
                    viewModel.uploadFile(it)
                }
            }
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { lesson ->
            binding.apply {
                Glide.with(root.context)
                    .load(lesson.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .into(coverImage)
                title.editText?.setText(lesson.title)
                description.editText?.setText(lesson.description)
                youtubeUrl.editText?.setText(if (lesson.content?.contains("youtube.com") == true || lesson.content?.contains("youtu.be") == true) lesson.content else null)

                questionAdapter.submitList(lesson.question)
                val ad: ArrayAdapter<String> = ArrayAdapter<String>(
                    root.context,
                    android.R.layout.simple_spinner_item,
                    (0..lesson.question.size).map { it.toString() }
                )
                ad.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                passingQuestions.adapter = ad
                if (lesson.passingQuestion < lesson.question.size) {
                    passingQuestions.setSelection(lesson.passingQuestion)
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
        viewModel.loadDrafts(courseId, lessonId)
    }
}