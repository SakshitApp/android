package com.sakshitapp.android.view.fragment.cart

import android.R.attr
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.sakshitapp.android.BuildConfig
import com.sakshitapp.android.R
import com.sakshitapp.android.adapter.CartAdapter
import com.sakshitapp.android.databinding.FragmentCartBinding
import com.sakshitapp.android.listener.PaymentCallback
import com.sakshitapp.android.view.MainStudentActivity
import com.sakshitapp.android.viewmodel.ViewModelFactory
import com.sakshitapp.shared.model.Course
import com.sakshitapp.shared.model.RazorPayOrder
import com.sakshitapp.shared.model.RazorPayVerify
import org.json.JSONObject


class CartFragment : Fragment(), PaymentResultWithDataListener {

    private val viewModel: CartViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentCartBinding? = null
    private lateinit var adapter: CartAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Checkout.preload(context)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        adapter = CartAdapter(object: CartAdapter.Callback{
            override fun onDelete(course: Course) = viewModel.delete(course.uuid)
        })
        binding.rv.adapter = adapter
        observe()
        binding.refresh.setOnRefreshListener {
            viewModel.load()
        }
        binding.buyNow.setOnClickListener {
            viewModel.buyNow()
        }
        return root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { data ->
            binding.apply {
                refresh.isRefreshing = false
                adapter.submitList(data)
                empty.visibility = if (!data.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
        viewModel.getOrder().observe(viewLifecycleOwner, { data ->
            data?.let {
                startPayment(it)
            }
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

    fun startPayment(data: RazorPayOrder) {
        val checkout = Checkout()
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY)
        checkout.setImage(R.drawable.ic_launcher_foreground)
        try {
            val options = JSONObject().apply {
                put("order_id", data.id)
                put("theme.color", "#7b1fa2")
                put("currency", data.currency)
                put("amount", data.amount)
            }
            (activity as? PaymentCallback)?.payListener = this
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e)
        }
    }

    companion object {
        private const val TAG = "CartFragment"
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        viewModel.verify(p1?.paymentId, p1?.signature)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        viewModel.verify(p2?.paymentId, p2?.signature)
    }
}