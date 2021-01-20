package com.kabbodev.educationalapp.ui.`interface`

import java.lang.Exception

interface FirebaseCallback {
    fun onSuccessListener()
    fun onFailureListener(e: Exception)
}