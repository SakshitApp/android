package com.sakshitapp.android.util

import android.view.View
import android.widget.TextView

fun TextView.setOrGone(text: String?) {
    if (!text.isNullOrEmpty()) {
        this.text = text
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}