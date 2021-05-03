package com.sakshitapp.android.view.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.sakshitapp.android.BuildConfig
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.CartAdapter
import com.sakshitapp.android.adapter.CourseAdapter
import com.sakshitapp.android.databinding.FragmentCartBinding
import com.sakshitapp.android.databinding.FragmentSearchBinding
import com.sakshitapp.android.view.MainStudentActivity
import com.sakshitapp.android.view.fragment.cart.CartViewModel
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.RazorPayOrder
import org.json.JSONObject

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentSearchBinding? = null
    private lateinit var adapter: CourseAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Checkout.preload(context)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        adapter = CourseAdapter(object : CourseAdapter.Callback{
            override fun onClick(course: Course) = findNavController()
                .navigate(R.id.action_navigation_search_to_courseFragment, bundleOf("courseId" to course.uuid))
        })
        binding.rv.adapter = adapter
        observe()
        binding.search.editText?.addTextChangedListener {
            viewModel.search(it?.toString())
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.apply {
                adapter.submitList(data)
                empty.visibility = if (!data.isNullOrEmpty()) View.GONE else View.VISIBLE
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
}