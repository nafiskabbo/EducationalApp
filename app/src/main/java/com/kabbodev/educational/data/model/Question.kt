package com.kabbodev.educational.data.model

data class Question(
    var answer: String? = "",
    var question: String? = "",
    var question_id: String? = "",
    var question_img: String? = "",
    var option_1: String? = "",
    var option_1_img: String? = "",
    var option_2: String? = "",
    var option_2_img: String? = "",
    var option_3: String? = "",
    var option_3_img: String? = "",
    var option_4: String? = "",
    var option_4_img: String? = "",
    var solve_link: String? = ""
)