package com.sakshitapp.android.view.fragment.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.LessonAdapter
import com.sakshitapp.android.adapter.ReviewAdapter
import com.sakshitapp.android.databinding.FragmentCourseBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Lesson


class CourseFragment : Fragment() {

    private val viewModel: CourseViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentCourseBinding? = null
    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root
        lessonAdapter = LessonAdapter(object: LessonAdapter.Callback{
            override fun onClick(lesson: Lesson) {}

        })
        binding.lesson.adapter = lessonAdapter
        reviewAdapter = ReviewAdapter()
        binding.reviewsRv.adapter = reviewAdapter
        observe()
        binding.review.setEndIconOnClickListener {
            viewModel.review(
                binding.review.editText?.text?.toString()
            )
        }
        binding.likeBtn.setOnClickListener {
            viewModel.like()
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.apply {
                Glide.with(root.context)
                    .load(data.course?.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .into(coverImage)
                lessonAdapter.locked = data.transactionId == null
                lessonAdapter.submitList(data?.course?.lessons)
                reviewAdapter.submitList(data?.course?.review)
                title.text = data.course?.title
                author.text = if (data.transactionId == null) {
                    getString(R.string.course_subtitle, data.course?.userName ?: "Unknown", data.course?.price)
                } else {
                    data.course?.userName ?: "Unknown"
                }
                likes.text = data.course?.likes.toString()
                likeBtn.isEnabled = if (data.isLiked) {
                    likeBtn.imageTintList = context?.let { ContextCompat.getColorStateList(it, R.color.purple_700) }
                    false
                } else {
                    true
                }
                description.text = data.course?.description
                data.course?.categories?.forEach {
                    addChipToView(it.name ?:"", categoryRv)
                }
                data.course?.languages?.forEach {
                    addChipToView(it.name ?:"",languageRv)
                }
                review.editText?.text = null
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
        arguments?.getString("courseId")?.let {
            viewModel.load(it)
        }
    }

    private fun addChipToView(language: String, group: ChipGroup) {
        val list = group.children.filter { (it as? Chip)?.text.toString() == language }
        if (list.count() <= 0) {
            getChip(language, group)
        }
    }

    private fun getChip(label: String, group: ChipGroup) {
        val chip = Chip(context, null, R.style.Widget_MaterialComponents_Chip_Entry).apply {
            text = label
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.purple_900)
        }
        chip.setOnCloseIconClickListener {
            group.removeView(chip)
        }
        group.addView(chip)
    }
}