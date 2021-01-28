package com.kabbodev.educational.ui.`interface`

import com.kabbodev.educational.data.model.User
import java.lang.Exception

interface FirebaseCallback {
    fun onSuccessListener(user: User? = null)
    fun onFailureListener(e: Exception)
}