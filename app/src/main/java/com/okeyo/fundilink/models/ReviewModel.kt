package com.okeyo.fundilink.models

data class ReviewModel(
    val id: String = "",
    val fundiId: String = "",
    val clientId: String = "",
    val jobId: String = "",
    val rating: Float = 0f,
    val comment: String = ""
)