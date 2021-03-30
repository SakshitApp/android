package com.sakshitapp.android.view.fragment.edit.lesson

import android.net.Uri

interface FileChooser {
    fun select(onSelection: (uri: Uri?) -> Unit)
}