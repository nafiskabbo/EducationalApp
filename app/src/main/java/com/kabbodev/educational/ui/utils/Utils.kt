package com.kabbodev.educational.ui.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).isSingleLine =
            false
    }.show()
}

fun startIntent(
    fromActivity: Activity,
    toActivity: Class<out Activity>,
    toFinish: Boolean
) {
    val intent = Intent(fromActivity, toActivity)
    fromActivity.startActivity(intent)
    if (toFinish) {
        fromActivity.finish()
    }
}

fun createDialog(
    context: Context,
    layoutResId: Int,
    drawableID: Int,
    cancellable: Boolean
): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(layoutResId)
    dialog.setCancelable(cancellable)
    dialog.window?.setBackgroundDrawable(
        ContextCompat.getDrawable(
            context,
            drawableID
        )
    )
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    return dialog
}

fun hasPermissions(context: Context, permission: String): Boolean {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}