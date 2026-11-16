package com.example.easykhata.data.models.sales

data class SaleItemDetail(
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val unitSymbol: String = "",
    val price: Double = 0.0,
    val total: Double = 0.0
)