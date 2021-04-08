package com.sakshitapp.android.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFilename(context: Context): String? {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf('/')
        if (cut != null || cut != -1) {
            result = result!!.substring(cut!! + 1)
        }
    }
    return result
}