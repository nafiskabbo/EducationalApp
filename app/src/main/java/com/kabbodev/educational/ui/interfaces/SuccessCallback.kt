package com.kabbodev.educational.ui.interfaces

import com.kabbodev.educational.data.model.Question

interface SuccessCallback {
    fun onSuccessListener(list: ArrayList<Question>)
}