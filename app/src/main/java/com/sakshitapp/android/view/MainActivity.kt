package com.sakshitapp.android.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.ActivityMainBinding
import com.sakshitapp.android.view.fragment.edit.course.ImageChooser
import com.sakshitapp.android.view.fragment.edit.lesson.FileChooser


class MainActivity : AppCompatActivity(), ImageChooser, FileChooser {

    private lateinit var binding: ActivityMainBinding
    private var onImageSelect: ((imageUri: Uri?) -> Unit)? = null
    private var onSelect: ((uri: Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> showBottomNav()
                R.id.navigation_dashboard -> showBottomNav()
                R.id.navigation_notifications -> showBottomNav()
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

    override fun selectImage(onSelection: (imageUri: Uri?) -> Unit) {
        // this starts the image picker
        // this starts the image picker
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(
            Intent.EXTRA_LOCAL_ONLY,
            true
        ) // this is the flag that does the trick

        startActivityForResult(photoPickerIntent, SELECT_PHOTO)
        onImageSelect = onSelection
    }

    override fun select(onSelection: (uri: Uri?) -> Unit) {
        val pickerIntent = Intent(Intent.ACTION_PICK)
        pickerIntent.putExtra(
            Intent.EXTRA_LOCAL_ONLY,
            true
        )

        startActivityForResult(pickerIntent, SELECT_FILES)
        onSelect = onSelection
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            onImageSelect?.let { it(data.data) }
        } else {
            onImageSelect?.let { it(null) }
        }
        if (requestCode == SELECT_FILES && resultCode == RESULT_OK && data != null) {
            onSelect?.let { it(data.data) }
        } else {
            onSelect?.let { it(null) }
        }
        onSelect = null
        onImageSelect = null
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

    companion object {
        private const val SELECT_PHOTO = 100
        private const val SELECT_FILES = 101
    }
}