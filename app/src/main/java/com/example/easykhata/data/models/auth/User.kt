package com.example.easykhata.data.models.auth
data class User(
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)