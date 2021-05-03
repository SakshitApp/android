package com.sakshitapp.android.view.fragment.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.adapter.NotificationAdapter
import com.sakshitapp.android.databinding.FragmentNotificationsBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory


class NotificationsFragment : Fragment() {

    private val viewModel: NotificationsViewModel by viewModels {
        ViewModelFactory()
    }
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = NotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        observe()
        binding.rv.adapter = adapter
        val decoration = DividerItemDecoration(
            binding.rv.context,
            DividerItemDecoration.VERTICAL
        )
        binding.rv.addItemDecoration(decoration)
        binding.refresh.setOnRefreshListener {
            viewModel.notifications()
        }
        return binding.root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.apply {
                refresh.isRefreshing = false
                adapter.submitList(data)
                empty.visibility = if (!data.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
        viewModel.error().observe(viewLifecycleOwner, {
            binding.refresh.isRefreshing = false
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
        viewModel.progress().observe(viewLifecycleOwner, {
            binding.refresh.isRefreshing = false
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
        viewModel.notifications()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}