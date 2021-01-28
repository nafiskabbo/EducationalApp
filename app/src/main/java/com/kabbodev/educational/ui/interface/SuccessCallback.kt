package com.kabbodev.educational.ui.`interface`

import com.kabbodev.educational.data.model.Question

interface SuccessCallback {
    fun onSuccessListener(list: ArrayList<Question>)
}