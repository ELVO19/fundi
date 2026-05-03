package com.okeyo.fundilink.models

data class BidModel(
    val id: String = "",
    val jobId: String = "",
    val fundiId: String = "",
    val amount: String = "",
    val message: String = "",
    val status: String = "pending"
)
