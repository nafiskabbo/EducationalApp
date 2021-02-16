package com.kabbodev.educational.ui.interfaces

import com.kabbodev.educational.data.model.Question
import java.lang.Exception

interface QuestionCallback {
    fun onSuccessListener(limitedList: ArrayList<Question>)
    fun onFailureListener(e: Exception)
}