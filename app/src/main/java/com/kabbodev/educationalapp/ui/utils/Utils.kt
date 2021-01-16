package com.kabbodev.educationalapp.ui.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.core.content.ContextCompat

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

fun createDialog(context: Context, layoutResId: Int, drawableID: Int, cancellable: Boolean): Dialog {
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