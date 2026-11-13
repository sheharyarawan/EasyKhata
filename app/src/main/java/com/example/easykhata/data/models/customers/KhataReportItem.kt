package com.example.easykhata.data.models.customers
import com.example.easykhata.data.models.expense.Expense
data class KhataReportItem(
    val expense: Expense,
    val customerName: String
)