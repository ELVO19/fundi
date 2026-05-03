package com.okeyo.fundilink.models

data class JobModel(
    val id: String = "",
    val clientId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val budget: String = "",
    val category: String = "",
    val status: String = "open",  // "open", "closed"
    val createdAt: String = ""
)
