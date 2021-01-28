package com.kabbodev.educational.ui.`interface`

interface StatsCallback {
    fun onSuccessListener(totalEmpty: Boolean)
    fun onFailureListener(e: Exception)
}