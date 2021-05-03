package com.sakshitapp.android.view.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.BigCourseAdapter
import com.sakshitapp.android.adapter.CourseStatusAdapter
import com.sakshitapp.android.databinding.FragmentHomeBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.Subscription

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: CourseStatusAdapter
    private lateinit var subsAdapter: BigCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        adapter = CourseStatusAdapter(object: CourseStatusAdapter.Callback{
            override fun onClick(course: Course) = findNavController()
                .navigate(R.id.action_navigation_home_to_courseFragment, bundleOf("courseId" to course.uuid, "willEdit" to true))

            override fun onEdit(course: Course) = findNavController()
                .navigate(R.id.action_navigation_home_to_editCourseFragment, bundleOf("courseId" to course.uuid))

            override fun onDelete(course: Course) = viewModel.delete(course)

            override fun onStateChange(course: Course, state: CourseState) = viewModel.changeState(course, state)

        })
        binding.courseRv.adapter = adapter
        binding.subscribedRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        subsAdapter = BigCourseAdapter(true, object: BigCourseAdapter.Callback{
            override fun onClick(course: Subscription) {
                findNavController()
                    .navigate(R.id.action_navigation_home_to_courseFragment, bundleOf("courseId" to course.courseId))
            }
        })
        binding.subscribedRv.adapter = subsAdapter
        binding.refresh.setOnRefreshListener {
            viewModel.loadDrafts(true)
        }

        val createCourseFab = binding.fabCreateCourse
        createCourseFab.setOnClickListener {
            findNavController()
                .navigate(R.id.action_navigation_home_to_editCourseFragment)
        }
        observe()
        return root
    }

    fun observe() {
        viewModel.getDrafts().observe(viewLifecycleOwner, { drafts ->
            binding.refresh.isRefreshing = false
            val subsVisibility = if (drafts.subscribed.isNotEmpty()) View.VISIBLE else View.GONE
            val corVisibility = if (drafts.courses.isNotEmpty()) View.VISIBLE else View.GONE
            binding.subscribedHeader.visibility = subsVisibility
            binding.subscribedRv.visibility = subsVisibility
            binding.courseHeader.visibility = corVisibility
            binding.courseRv.visibility = corVisibility
            subsAdapter.submitList(drafts.subscribed)
            adapter.submitList(drafts.courses)
        })
        viewModel.error().observe(viewLifecycleOwner, {
            binding.refresh.isRefreshing = false
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