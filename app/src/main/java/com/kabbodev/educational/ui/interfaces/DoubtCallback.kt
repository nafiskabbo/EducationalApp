package com.kabbodev.educational.ui.interfaces

import com.kabbodev.educational.data.model.Doubt

interface DoubtCallback {
    fun onSuccessListener(totalEmpty: Boolean, doubtList: List<Doubt>?)
    fun onFailureListener(e: Exception)
}