package com.sakshitapp.android.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ActivityMainBinding
import com.sakshitapp.android.databinding.ActivityMainStudentBinding

class MainStudentActivity: AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityMainStudentBinding

    var payListener: PaymentResultWithDataListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_cart
            )
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> showBottomNav()
                R.id.navigation_dashboard -> showBottomNav()
                R.id.navigation_notifications -> showBottomNav()
                R.id.navigation_cart -> showBottomNav()
                else -> hideBottomNav()
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        payListener?.onPaymentSuccess(p0, p1)
    }
    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        payListener?.onPaymentError(p0, p1, p2)
    }
}