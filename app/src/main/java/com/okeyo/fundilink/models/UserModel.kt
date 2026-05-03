package com.okeyo.fundilink.models

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",  // "client", "fundi", "admin"
    val phone: String = "",
    val location: String = "",
    val profileImage: String = "",
    val rating: Float = 0f
)
