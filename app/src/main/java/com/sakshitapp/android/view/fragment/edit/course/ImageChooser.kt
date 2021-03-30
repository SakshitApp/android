package com.sakshitapp.android.view.fragment.edit.course

import android.net.Uri

interface ImageChooser {
    fun selectImage(onSelection: (imageUri: Uri?) -> Unit)
}