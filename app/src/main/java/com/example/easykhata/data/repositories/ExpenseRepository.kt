package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.expense.Expense
interface ExpenseRepository {
    fun addExpense(
        businessId: String,
        expense: Expense,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
}