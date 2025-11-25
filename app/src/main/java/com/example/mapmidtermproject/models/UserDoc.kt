package com.example.mapmidtermproject.models

data class UserDoc(
    val uid: String = "",
    val name: String = "",
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
