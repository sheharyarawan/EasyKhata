package com.example.easykhata.ui.viewModels.account
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.ReminderStatus
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.WasooliReminder
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.data.repositories.WasooliRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject
@HiltViewModel
class WasooliViewModel @Inject constructor(
    private val wasooliRepository: WasooliRepository
) : ViewModel() {
    private val _upcomingRemindersState =
        MutableStateFlow<UiState<List<WasooliReminder>>>(UiState.Idle)
    val upcomingRemindersState = _upcomingRemindersState.asStateFlow()
    private val _lateRemindersState =
        MutableStateFlow<UiState<List<WasooliReminder>>>(UiState.Idle)
    val lateRemindersState = _lateRemindersState.asStateFlow()
    private val _eligibleCustomersState =
        MutableStateFlow<UiState<List<CustomerWithBalance>>>(UiState.Idle)
    val eligibleCustomersState = _eligibleCustomersState.asStateFlow()
    private val _saveReminderState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val saveReminderState = _saveReminderState.asStateFlow()
    private val _deleteReminderState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteReminderState = _deleteReminderState.asStateFlow()
    private var customers = emptyList<CustomerWithBalance>()
    private var upcomingReminders = emptyList<WasooliReminder>()
    private var lateReminders = emptyList<WasooliReminder>()
    private var customersLoaded = false
    private var remindersLoaded = false
    private var searchQuery = ""
    fun updateCustomers(customers: List<CustomerWithBalance>) {
        this.customers = customers
        customersLoaded = true
        updateEligibleCustomers()
    }
    fun setCustomerSearchQuery(query: String) {
        searchQuery = query
        updateEligibleCustomers()
    }
    private fun updateEligibleCustomers() {
        if (!customersLoaded || !remindersLoaded) {
            _eligibleCustomersState.value = UiState.Loading
            return
        }
        val reminderCustomerIds = (upcomingReminders + lateReminders)
            .map { it.customerId }
            .toSet()
        val eligibleCustomers = customers.filter { customer ->
            customer.balance > 0.0 &&
                    customer.customer.id !in reminderCustomerIds &&
                    customer.customer.name.contains(
                        searchQuery.trim(),
                        ignoreCase = true
                    )
        }
        _eligibleCustomersState.value = UiState.Success(eligibleCustomers)
    }
    fun loadReminders(businessId: String) {
        if (businessId.isBlank()) return
        remindersLoaded = false
        _upcomingRemindersState.value = UiState.Loading
        _lateRemindersState.value = UiState.Loading
        _eligibleCustomersState.value = UiState.Loading
        wasooliRepository.observeReminders(
            businessId = businessId,
            onSuccess = { reminders ->
                separateReminders(reminders)
            },
            onFailure = { exception ->
                val error = UiState.Error(
                    exception.message ?: "Unable to load reminders"
                )
                _upcomingRemindersState.value = error
                _lateRemindersState.value = error
                _eligibleCustomersState.value = error
            }
        )
    }
    private fun separateReminders(reminders: List<WasooliReminder>) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val pendingReminders = reminders.filter {
            it.status == ReminderStatus.PENDING &&
                    it.reminderDate != null
        }
        upcomingReminders = pendingReminders
            .filter { it.reminderDate!!.toDate().time >= today }
            .sortedBy { it.reminderDate!!.toDate().time }
        lateReminders = pendingReminders
            .filter { it.reminderDate!!.toDate().time < today }
            .sortedByDescending { it.reminderDate!!.toDate().time }
        remindersLoaded = true
        _upcomingRemindersState.value =
            UiState.Success(upcomingReminders)
        _lateRemindersState.value =
            UiState.Success(lateReminders)
        updateEligibleCustomers()
    }
    fun saveReminder(
        businessId: String,
        customerId: String,
        reminderDate: Timestamp
    ) {
        if (businessId.isBlank() || customerId.isBlank()) {
            _saveReminderState.value =
                UiState.Error("Invalid business or customer")
            return
        }
        _saveReminderState.value = UiState.Loading
        val now = Timestamp.now()
        val reminder = WasooliReminder(
            customerId = customerId,
            reminderDate = reminderDate,
            status = ReminderStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
        wasooliRepository.addReminder(
            businessId = businessId,
            reminder = reminder,
            onSuccess = { reminderId ->
                _saveReminderState.value =
                    UiState.Success(reminderId)
            },
            onFailure = { exception ->
                _saveReminderState.value = UiState.Error(
                    exception.message ?: "Unable to save reminder"
                )
            }
        )
    }
    fun deleteReminder(
        businessId: String,
        customerId: String
    ) {
        if (businessId.isBlank() || customerId.isBlank()) {
            _deleteReminderState.value =
                UiState.Error("Invalid business or customer")
            return
        }
        _deleteReminderState.value = UiState.Loading
        wasooliRepository.deleteReminder(
            businessId = businessId,
            customerId = customerId,
            onSuccess = {
                _deleteReminderState.value = UiState.Success(Unit)
            },
            onFailure = { exception ->
                _deleteReminderState.value = UiState.Error(
                    exception.message ?: "Unable to delete reminder"
                )
            }
        )
    }
    fun resetReminderOperationStates() {
        _saveReminderState.value = UiState.Idle
        _deleteReminderState.value = UiState.Idle
    }
    override fun onCleared() {
        super.onCleared()
        wasooliRepository.removeRemindersListener()
    }
}
