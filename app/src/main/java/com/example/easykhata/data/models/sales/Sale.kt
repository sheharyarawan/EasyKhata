package com.example.easykhata.data.models.sales
import com.example.easykhata.data.models.PaymentMethod
data class Sale(
    val id: String = "",
    val invoiceNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val items: List<SaleItemDetail> = emptyList(),
    val date: Long = 0L,
    val time: String = "",
    val totalPrice: Double = 0.0,
    val discount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.NAQAD,
    val note: String = ""
)