package com.example.easykhata.data.models.auth
data class Business(
    val businessId: String = "",
    val businessName: String = "",
    val businessType: String = "",
    val businessAddress: String = "",
    val createdAt: Long = System.currentTimeMillis()
)