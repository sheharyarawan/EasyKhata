package com.example.easykhata.ui.viewModels.customer
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.data.models.expense.CustomerBalanceFilter
import com.example.easykhata.data.models.expense.Expense
import com.example.easykhata.data.repositories.CustomerRepository
import com.example.easykhata.data.repositories.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _customersState =
        MutableStateFlow<UiState<List<CustomerWithBalance>>>(UiState.Idle)
    val customersState = _customersState.asStateFlow()
    private val _customerFilter =
        MutableStateFlow(CustomerBalanceFilter.ALL)
    val customerFilter = _customerFilter.asStateFlow()
    private var originalCustomers: List<CustomerWithBalance> = emptyList()
    private val _totalLene = MutableStateFlow(0.0)
    val totalLene = _totalLene.asStateFlow()
    private val _totalDene = MutableStateFlow(0.0)
    val totalDene = _totalDene.asStateFlow()
    private var customers = emptyList<Customer>()
    private var expenses = emptyList<Expense>()
    fun observeHomeData(businessId: String) {
        _customersState.value = UiState.Loading
        customerRepository.observeCustomers(
            businessId = businessId,
            onSuccess = { customerList ->
                customers = customerList
                calculateCustomerBalances()
            },
            onFailure = { exception ->
                _customersState.value = UiState.Error(
                    exception.message ?: "Unable to load customers"
                )
            }
        )
        expenseRepository.observeBusinessExpenses(
            businessId = businessId,
            onSuccess = { expenseList ->
                expenses = expenseList
                calculateCustomerBalances()
            },
            onFailure = { exception ->
                _customersState.value = UiState.Error(
                    exception.message ?: "Unable to load expenses"
                )
            }
        )
    }
    private fun calculateCustomerBalances() {
        originalCustomers = customers.map { customer ->
            val customerExpenses = expenses.filter {
                it.customerId == customer.id
            }
            val balance = customerExpenses.sumOf { expense ->
                when (expense.direction) {
                    ExpenseDirection.DIYE -> expense.amount
                    ExpenseDirection.LIYE -> -expense.amount
                }
            }
            val lastTransaction = customerExpenses
                .maxByOrNull { it.date?.seconds ?: 0L }
                ?.date
            CustomerWithBalance(
                customer = customer,
                balance = balance,
                lastTransaction = lastTransaction
            )
        }
        _totalLene.value = originalCustomers
            .filter { it.balance > 0 }
            .sumOf { it.balance }
        _totalDene.value = originalCustomers
            .filter { it.balance < 0 }
            .sumOf { -it.balance }
        applyCustomerFilter()
    }
    fun setCustomerFilter(filter: CustomerBalanceFilter) {
        _customerFilter.value = filter
        applyCustomerFilter()
    }
    private fun applyCustomerFilter() {
        val filteredCustomers = when (_customerFilter.value) {
            CustomerBalanceFilter.ALL -> originalCustomers
            CustomerBalanceFilter.CLEAR ->
                originalCustomers.filter { it.balance == 0.0 }
            CustomerBalanceFilter.MAINE_DENE ->
                originalCustomers.filter { it.balance < 0.0 }
            CustomerBalanceFilter.MAINE_LENE ->
                originalCustomers.filter { it.balance > 0.0 }
        }
        _customersState.value = UiState.Success(filteredCustomers)
    }
    override fun onCleared() {
        super.onCleared()
        customerRepository.removeCustomersListener()
        expenseRepository.removeBusinessExpensesListener()
    }
}