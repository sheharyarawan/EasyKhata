package com.example.easykhata.data.models.expense
data class ExpenseWithBalance(
    val expense: Expense,
    val runningBalance: Double
)