package com.sakshitapp.android.view.fragment.edit.course

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.LessonStatusAdapter
import com.sakshitapp.android.databinding.FragmentEditCourseBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Lesson

class EditCourseFragment : Fragment() {

    private val viewModel: EditCourseViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentEditCourseBinding? = null
    private lateinit var lessonAdapter: LessonStatusAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root
        lessonAdapter = LessonStatusAdapter(object: LessonStatusAdapter.Callback{
            override fun onNew() = findNavController()
                .navigate(R.id.action_editCourseFragment_to_editLessonFragment, bundleOf("courseId" to viewModel.getDraft().value?.uuid))

            override fun onClick(lesson: Lesson) = findNavController()
                .navigate(R.id.action_editCourseFragment_to_editLessonFragment, bundleOf("courseId" to viewModel.getDraft().value?.uuid, "lessonId" to lesson.uuid))

            override fun onDelete(lesson: Lesson) = viewModel.removeLesson(lesson)

        })
        binding.lesson.adapter = lessonAdapter
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
                binding.summery.editText?.text?.toString(),
                binding.description.editText?.text?.toString(),
                binding.categoryRv.children.filter { it is Chip }.map { (it as Chip).text.toString() }.toList(),
                binding.languageRv.children.filter { it is Chip }.map { (it as Chip).text.toString() }.toList(),
                binding.price.editText?.text?.toString()?.toDoubleOrNull()
            )
        }
        return root
    }

    private fun observe() {
        viewModel.getDraft().observe(viewLifecycleOwner, { draft ->
            binding.apply {
                Glide.with(root.context)
                    .load(draft.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fallback(R.drawable.ic_baseline_image_24)
                    .into(coverImage)
                title.editText?.setText(draft.title)
                summery.editText?.setText(draft.summery)
                description.editText?.setText(draft.description)
                val categoryAdapter = ArrayAdapter(requireContext(), R.layout.list_item, draft.categoriesAll.map { it.name })
                (addCategory.editText as? AutoCompleteTextView)?.setAdapter(categoryAdapter)
                (addCategory.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, _, _ ->
                    addCategory.editText?.text?.toString()?.let { category ->
                        addChipToView(category, binding.categoryRv)
                        addCategory.editText?.text = null
                    }
                }
                addCategory.editText?.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = categoryAdapter.remove(s.toString())
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = categoryAdapter.add(s.toString())
                    override fun afterTextChanged(s: Editable?) {}
                })
                draft.categories.forEach {
                    addChipToView(it.name ?:"", categoryRv)
                }

                val languageAdapter = ArrayAdapter(requireContext(), R.layout.list_item, draft.languagesAll.map { it.name })
                (addLanguage.editText as? AutoCompleteTextView)?.setAdapter(languageAdapter)
                (addLanguage.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, _, _ ->
                    addLanguage.editText?.text?.toString()?.let { language ->
                        addChipToView(language, binding.languageRv)
                        addLanguage.editText?.text = null
                    }
                }
                addLanguage.editText?.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = languageAdapter.remove(s.toString())
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)  = languageAdapter.add(s.toString())
                    override fun afterTextChanged(s: Editable?) {}
                })
                draft.languages.forEach {
                    addChipToView(it.name ?:"",languageRv)
                }
                price.editText?.setText(draft.price.toString())
                lessonAdapter.submitList(draft.lessons)
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
        val id = arguments?.getString("courseId")
        viewModel.loadDrafts(id)
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
            isCloseIconVisible = true
        }
        chip.setOnCloseIconClickListener {
            group.removeView(chip)
        }
        group.addView(chip)
    }
}