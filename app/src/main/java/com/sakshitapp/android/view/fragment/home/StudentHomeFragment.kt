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
import com.sakshitapp.android.adapter.CourseAdapter
import com.sakshitapp.android.adapter.CourseStatusAdapter
import com.sakshitapp.android.databinding.FragmentHomeBinding
import com.sakshitapp.android.databinding.FragmentStudentHomeBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.CourseState
import com.sakshitapp.shared.model.Subscription

class StudentHomeFragment : Fragment() {

    private val viewModel: StudentHomeViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentStudentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: BigCourseAdapter
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.subscribedRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        adapter = BigCourseAdapter(true, object: BigCourseAdapter.Callback{
            override fun onClick(course: Subscription) {
                findNavController()
                    .navigate(R.id.action_navigation_home_to_courseFragment, bundleOf("courseId" to course.courseId))
            }
        })
        courseAdapter = CourseAdapter(object: CourseAdapter.Callback{
            override fun onClick(course: Course) {
                findNavController()
                    .navigate(R.id.action_navigation_home_to_courseFragment, bundleOf("courseId" to course.uuid))
            }

        })
        binding.subscribedRv.adapter = adapter
        binding.courseRv.adapter = courseAdapter
        binding.refresh.setOnRefreshListener {
            viewModel.load(true)
        }

        observe()
        return root
    }

    fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.refresh.isRefreshing = false
            if (data.subscribed.isNotEmpty()) {
                binding.subscribedHeader.visibility = View.VISIBLE
                binding.subscribedRv.visibility = View.VISIBLE
            } else {
                binding.subscribedHeader.visibility = View.GONE
                binding.subscribedRv.visibility = View.GONE
            }
            adapter.submitList(data.subscribed)
            if (data.courses.isNotEmpty()) {
                binding.courseHeader.visibility = View.VISIBLE
                binding.courseRv.visibility = View.VISIBLE
            } else {
                binding.courseHeader.visibility = View.GONE
                binding.courseRv.visibility = View.GONE
            }
            courseAdapter.submitList(data.courses)
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