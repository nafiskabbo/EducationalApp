package com.kabbodev.educational.ui.interfaces

import com.kabbodev.educational.data.model.QuestionByUser
import com.kabbodev.educational.data.model.Recap
import java.lang.Exception

interface RecapCallback {
    fun onSuccessListener(recap: Recap?, questionsList: ArrayList<QuestionByUser>? = null)
    fun onFailureListener(e: Exception)
}