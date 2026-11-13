package com.example.easykhata.ui.viewModels.expense
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.KhataReportFilter
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.expense.Expense
import com.example.easykhata.data.repositories.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
@HiltViewModel
class KhataReportViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _reportState =
        MutableStateFlow<UiState<List<Expense>>>(UiState.Idle)
    val reportState = _reportState.asStateFlow()
    private val _selectedStartDate = MutableStateFlow<Date?>(null)
    val selectedStartDate = _selectedStartDate.asStateFlow()
    private val _selectedEndDate = MutableStateFlow<Date?>(null)
    val selectedEndDate = _selectedEndDate.asStateFlow()
    private val _openSingleDayPicker = MutableStateFlow(false)
    val openSingleDayPicker = _openSingleDayPicker.asStateFlow()
    private val _totalDiye = MutableStateFlow(0.0)
    val totalDiye = _totalDiye.asStateFlow()
    private val _totalLiye = MutableStateFlow(0.0)
    val totalLiye = _totalLiye.asStateFlow()
    private var allExpenses: List<Expense> = emptyList()
    private var startDate: Date? = null
    private var endDate: Date? = null
    fun observeReportExpenses(businessId: String) {
        _reportState.value = UiState.Loading
        expenseRepository.observeReportExpenses(
            businessId = businessId,
            onSuccess = { expenses ->
                allExpenses = expenses
                applyFilter()
            },
            onFailure = { exception ->
                _reportState.value = UiState.Error(
                    exception.message ?: "Something went wrong"
                )
            }
        )
    }
    fun setFilter(filter: KhataReportFilter) {
        if (filter == KhataReportFilter.SINGLE_DAY) {
            _openSingleDayPicker.value = true
            return
        }
        _selectedStartDate.value = null
        _selectedEndDate.value = null
        startDate = null
        endDate = null
        when (filter) {
            KhataReportFilter.ALL -> Unit
            KhataReportFilter.SINGLE_DAY -> return
            KhataReportFilter.PICHLA_HAFTA -> {
                startDate = getPastDate(6)
                endDate = getDayBoundary(Date(), endOfDay = true)
            }

            KhataReportFilter.PICHLA_MAHINA -> {
                startDate = getPastDate(29)
                endDate = getDayBoundary(Date(), endOfDay = true)
            }
        }
        applyFilter()
    }
    fun setSingleDay(date: Date) {
        startDate = getDayBoundary(date)
        endDate = getDayBoundary(date, endOfDay = true)
        _selectedStartDate.value = date
        _selectedEndDate.value = date
        applyFilter()
    }
    fun setStartDate(date: Date) {
        startDate = getDayBoundary(date)
        endDate = null
        _selectedStartDate.value = date
        _selectedEndDate.value = null
        applyFilter()
    }
    fun setEndDate(date: Date) {
        val currentStartDate = startDate ?: return
        if (date.before(currentStartDate)) return
        endDate = getDayBoundary(date, endOfDay = true)
        _selectedEndDate.value = date
        applyFilter()
    }
    fun resetSingleDayPicker() {
        _openSingleDayPicker.value = false
    }
    private fun applyFilter() {
        val filteredExpenses = allExpenses
            .filter { expense ->
                val date = expense.date?.toDate() ?: return@filter false
                (startDate == null || !date.before(startDate)) &&
                        (endDate == null || !date.after(endDate))
            }
            .sortedByDescending { it.date?.toDate() }
        _totalDiye.value = filteredExpenses
            .filter { it.direction == ExpenseDirection.DIYE }
            .sumOf { it.amount }
        _totalLiye.value = filteredExpenses
            .filter { it.direction == ExpenseDirection.LIYE }
            .sumOf { it.amount }
        _reportState.value = UiState.Success(filteredExpenses)
    }
    private fun getPastDate(days: Int): Date {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return getDayBoundary(calendar.time)
    }
    private fun getDayBoundary(
        date: Date,
        endOfDay: Boolean = false
    ): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, if (endOfDay) 23 else 0)
            set(Calendar.MINUTE, if (endOfDay) 59 else 0)
            set(Calendar.SECOND, if (endOfDay) 59 else 0)
            set(Calendar.MILLISECOND, if (endOfDay) 999 else 0)
        }.time
    }
    override fun onCleared() {
        expenseRepository.removeReportExpensesListener()
        super.onCleared()
    }
}
