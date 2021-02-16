package com.kabbodev.educational.data.model

data class Plan(
    var id: String? = "",
    var type: String? = "",
    var title: String? = "",
    var subtitle: String? = "",
    var class_name: String? = "",
    var price: String? = "",
    var chapter_set: String? = "",
    var contact_info: String? = "",
    var payment_link: String? = "",
    var join_class_link: String? = ""
)