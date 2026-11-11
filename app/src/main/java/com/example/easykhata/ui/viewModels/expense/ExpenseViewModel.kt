package com.example.easykhata.ui.viewModels.expense
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.ExpenseSortOption
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.expense.Expense
import com.example.easykhata.data.models.expense.ExpenseWithBalance
import com.example.easykhata.data.repositories.ExpenseRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _addExpenseState =
        MutableStateFlow<UiState<String>?>(null)
    val addExpenseState: StateFlow<UiState<String>?> =
        _addExpenseState.asStateFlow()
    private val _customerExpensesState =
        MutableStateFlow<UiState<List<ExpenseWithBalance>>>(UiState.Idle)
    val customerExpensesState: StateFlow<UiState<List<ExpenseWithBalance>>> =
        _customerExpensesState.asStateFlow()
    private val _updateExpenseState =
        MutableStateFlow<UiState<Expense>>(UiState.Idle)
    val updateExpenseState = _updateExpenseState.asStateFlow()
    private val _deleteExpenseState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteExpenseState = _deleteExpenseState.asStateFlow()
    private val _sortOption =
        MutableStateFlow(ExpenseSortOption.DEFAULT)
    val sortOption: StateFlow<ExpenseSortOption> =
        _sortOption.asStateFlow()
    private val _runningBalance = MutableStateFlow(0.0)
    val runningBalance: StateFlow<Double> = _runningBalance.asStateFlow()
    private var originalExpenses: List<ExpenseWithBalance> = emptyList()
    fun addExpense(
        businessId: String,
        expense: Expense
    ) {
        if (businessId.isBlank()) {
            _addExpenseState.value =
                UiState.Error("Business not selected")
            return
        }
        if (expense.amount <= 0) {
            _addExpenseState.value =
                UiState.Error("Amount must be greater than zero")
            return
        }
        _addExpenseState.value = UiState.Loading
        expenseRepository.addExpense(
            businessId = businessId,
            expense = expense.copy(
                date = expense.date ?: Timestamp.now()
            ),
            onSuccess = { id ->
                _addExpenseState.value = UiState.Success(id)
            },
            onFailure = { exception ->
                _addExpenseState.value =
                    UiState.Error(exception.message ?: "Something went wrong")
            }
        )
    }
    fun loadCustomerExpenses(
        businessId: String,
        customerId: String
    ) {
        if (businessId.isBlank() || customerId.isBlank()) {
            _customerExpensesState.value =
                UiState.Error("Business or customer not selected")
            return
        }
        _runningBalance.value = 0.0
        _customerExpensesState.value = UiState.Loading
        expenseRepository.getCustomerExpenses(
            businessId = businessId,
            customerId = customerId,
            onSuccess = { expenses ->
                calculateRunningBalances(expenses)
                applySorting()
            },
            onFailure = { exception ->
                _customerExpensesState.value =
                    UiState.Error(exception.message ?: "Something went wrong")
            }
        )
    }
    private fun calculateRunningBalances(expenses: List<Expense>) {
        var runningBalance = 0.0
        originalExpenses = expenses
            .sortedWith(
                compareBy<Expense> { it.date?.seconds ?: 0L }
                    .thenBy { it.date?.nanoseconds ?: 0 }
                    .thenBy { it.id }
            )
            .map { expense ->
                runningBalance += when (expense.direction) {
                    ExpenseDirection.DIYE -> expense.amount
                    ExpenseDirection.LIYE -> -expense.amount
                }
                ExpenseWithBalance(
                    expense = expense,
                    runningBalance = runningBalance
                )
            }
        _runningBalance.value = originalExpenses.lastOrNull()?.runningBalance ?: 0.0
    }
    private fun applySorting() {
        val sortedExpenses = when (_sortOption.value) {
            ExpenseSortOption.DEFAULT,
            ExpenseSortOption.NEWEST_FIRST -> {
                originalExpenses.sortedWith(
                    compareByDescending<ExpenseWithBalance> {
                        it.expense.date?.seconds ?: 0L
                    }.thenByDescending {
                        it.expense.date?.nanoseconds ?: 0
                    }.thenByDescending {
                        it.expense.id
                    }
                )
            }
            ExpenseSortOption.OLDEST_FIRST -> {
                originalExpenses
            }
            ExpenseSortOption.AMOUNT_HIGH_TO_LOW -> {
                originalExpenses.sortedByDescending {
                    it.expense.amount
                }
            }
            ExpenseSortOption.AMOUNT_LOW_TO_HIGH -> {
                originalExpenses.sortedBy {
                    it.expense.amount
                }
            }
        }
        _customerExpensesState.value = UiState.Success(sortedExpenses)
    }
    fun updateExpense(
        businessId: String,
        expense: Expense
    ) {
        if (businessId.isBlank() || expense.id.isBlank()) {
            _updateExpenseState.value =
                UiState.Error("Business or expense not selected")
            return
        }
        if (expense.amount <= 0) {
            _updateExpenseState.value =
                UiState.Error("Amount must be greater than zero")
            return
        }
        _updateExpenseState.value = UiState.Loading
        expenseRepository.updateExpense(
            businessId = businessId,
            expense = expense,
            onSuccess = {
                _updateExpenseState.value = UiState.Success(expense)
            },
            onFailure = { exception ->
                _updateExpenseState.value = UiState.Error(
                    exception.message ?: "Unable to update expense"
                )
            }
        )
    }
    fun deleteExpense(
        businessId: String,
        expenseId: String
    ) {
        _deleteExpenseState.value = UiState.Loading
        expenseRepository.deleteExpense(
            businessId = businessId,
            expenseId = expenseId,
            onSuccess = {
                _deleteExpenseState.value = UiState.Success(Unit)
            },
            onFailure = { exception ->
                _deleteExpenseState.value =
                    UiState.Error(exception.message ?: "Failed to delete expense")
            }
        )
    }
    fun setSortOption(option: ExpenseSortOption) {
        _sortOption.value = option
        applySorting()
    }
    fun resetAddExpenseState() {
        _addExpenseState.value = null
    }
    fun resetUpdateExpenseState() {
        _updateExpenseState.value = UiState.Idle
    }
    fun resetDeleteExpenseState() {
        _deleteExpenseState.value = UiState.Idle
    }
    override fun onCleared() {
        super.onCleared()
        expenseRepository.removeCustomerExpensesListener()
    }
}