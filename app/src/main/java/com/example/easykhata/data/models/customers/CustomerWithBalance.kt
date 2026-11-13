package com.example.easykhata.data.models.customers
import com.google.firebase.Timestamp
data class CustomerWithBalance(
    val customer: Customer,
    val balance: Double = 0.0,
    val lastTransaction: Timestamp? = null
)