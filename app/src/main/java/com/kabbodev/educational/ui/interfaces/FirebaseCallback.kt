package com.kabbodev.educational.ui.interfaces

import com.kabbodev.educational.data.model.User
import java.lang.Exception

interface FirebaseCallback {
    fun onSuccessListener(user: User? = null)
    fun onFailureListener(e: Exception)
}