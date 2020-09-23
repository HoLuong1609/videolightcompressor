package com.example.videolightcompressor.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.videolightcompressor.ui.VideoCompressorApplication
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

val appContext = VideoCompressorApplication.instance

fun Activity.showSnackBar(@StringRes resId: Int) {
    Snackbar.make(window.decorView.rootView, resId, Snackbar.LENGTH_SHORT).show()
}

fun Activity.showSnackBar(message: String) {
    Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT).show()
}

fun Activity.openStore() {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
        )
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Context.toast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showAlertDialog(
    title: String = "",
    msg: String = "",
    positiveButton: String = getString(android.R.string.ok),
    negativeButton: String = getString(android.R.string.cancel),
    onPositiveButtonClick: View.OnClickListener? = null,
    onNegativeButtonClick: View.OnClickListener? = null,
    isCanTouchOutside: Boolean = false,
    isCancelable: Boolean = true
) {
    val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
    alertDialog.setTitle(title)
    alertDialog.setMessage(msg)
    alertDialog.setCanceledOnTouchOutside(isCanTouchOutside)
    alertDialog.setCancelable(isCancelable)
    alertDialog.setButton(
        AlertDialog.BUTTON_POSITIVE, positiveButton
    ) { dialog, _ ->
        run {
            dialog.dismiss()
            onPositiveButtonClick?.onClick(null)
        }
    }
    onNegativeButtonClick?.let {
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, negativeButton
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                it.onClick(null)
            }
        }
    }
    alertDialog.show()
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}