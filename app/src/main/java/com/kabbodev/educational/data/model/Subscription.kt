package com.kabbodev.educational.data.model

data class Subscription(
    var plan: Plan? = null,
    var chapter: List<Chapter>? = emptyList()
)
