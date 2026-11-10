package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.expense.Expense
interface ExpenseRepository {
    fun addExpense(
        businessId: String,
        expense: Expense,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getCustomerExpenses(
        businessId: String,
        customerId: String,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun removeCustomerExpensesListener()
    fun updateExpense(
        businessId: String,
        expense: Expense,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}