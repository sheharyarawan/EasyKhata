package com.example.easykhata.data.models.expense

import com.example.easykhata.data.models.ExpenseDirection
import com.google.firebase.Timestamp
data class Expense(
    val id: String = "",
    val customerId: String? = null,
    val date: Timestamp? = null,
    val description: String = "",
    val amount: Double = 0.0,
    val direction: ExpenseDirection = ExpenseDirection.LIYE
)