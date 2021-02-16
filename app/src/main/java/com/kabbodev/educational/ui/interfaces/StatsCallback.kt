package com.kabbodev.educational.ui.interfaces

interface StatsCallback {
    fun onSuccessListener(totalEmpty: Boolean, downloadFile: String? = null)
    fun onFailureListener(e: Exception)
}