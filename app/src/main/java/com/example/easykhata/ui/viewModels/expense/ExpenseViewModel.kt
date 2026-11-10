package com.example.easykhata.ui.viewModels.expense
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.expense.Expense
import com.example.easykhata.data.repositories.ExpenseRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _addExpenseState =
        MutableStateFlow<UiState<String>?>(null)
    val addExpenseState = _addExpenseState.asStateFlow()
    fun addExpense(
        businessId: String,
        expense: Expense
    ) {
        if (businessId.isBlank()) {
            _addExpenseState.value =
                UiState.Error("Business not selected")
            return
        }
        if (expense.amount <= 0.0) {
            _addExpenseState.value =
                UiState.Error("Enter a valid amount")
            return
        }
        _addExpenseState.value = UiState.Loading
        val expenseToSave = expense.copy(
            date = expense.date ?: Timestamp.now()
        )
        expenseRepository.addExpense(
            businessId = businessId,
            expense = expenseToSave,
            onSuccess = { expenseId ->
                _addExpenseState.value =
                    UiState.Success(expenseId)
            },
            onFailure = { exception ->
                _addExpenseState.value = UiState.Error(
                    exception.message ?: "Unable to add expense"
                )
            }
        )
    }
    fun resetAddExpenseState() {
        _addExpenseState.value = null
    }
}