package com.example.easykhata.data.models.customers

import com.google.firebase.Timestamp
data class Customer(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val createdAt: Timestamp? = null
)