package com.example.videolightcompressor.extensions

import android.app.Activity
import androidx.annotation.StringRes
import com.example.videolightcompressor.ui.VideoCompressorApplication
import com.google.android.material.snackbar.Snackbar

val appContext = VideoCompressorApplication.instance

fun Activity.showSnackBar(@StringRes resId: Int) {
    Snackbar.make(window.decorView.rootView, resId, Snackbar.LENGTH_SHORT).show()
}

fun Activity.showSnackBar(message: String) {
    Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT).show()
}