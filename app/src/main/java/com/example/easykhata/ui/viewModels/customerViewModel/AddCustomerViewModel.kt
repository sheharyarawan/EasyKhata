package com.example.easykhata.ui.viewModels.customerViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.repositories.CustomerRepository
import com.example.easykhata.data.repositories.PhoneContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val phoneContactsRepository: PhoneContactsRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {
    private val _contactsState =
        MutableStateFlow<UiState<List<Contact>>>(UiState.Loading)
    val contactsState = _contactsState.asStateFlow()
    private val _addCustomerState =
        MutableStateFlow<UiState<String>?>(null)
    val addCustomerState = _addCustomerState.asStateFlow()
    private val _existingCustomerPhones =
        MutableStateFlow<Set<String>>(emptySet())
    val existingCustomerPhones = _existingCustomerPhones.asStateFlow()
    fun loadContacts() {
        viewModelScope.launch {
            _contactsState.value = UiState.Loading
            try {
                val contacts = phoneContactsRepository.getPhoneContacts()
                _contactsState.value = UiState.Success(contacts)
            } catch (e: Exception) {
                _contactsState.value = UiState.Error(
                    e.message ?: "Unable to load contacts"
                )
            }
        }
    }
    fun loadExistingCustomers(businessId: String) {
        customerRepository.getCustomers(
            businessId = businessId,
            onSuccess = { customers ->
                _existingCustomerPhones.value = customers
                    .map { it.phone.filter(Char::isDigit) }
                    .toSet()
            },
            onFailure = {
                _existingCustomerPhones.value = emptySet()
            }
        )
    }
    fun addCustomer(businessId: String, customer: Customer) {
        if (businessId.isBlank()) {
            _addCustomerState.value = UiState.Error(
                "Please select a business first"
            )
            return
        }
        _addCustomerState.value = UiState.Loading
        customerRepository.addCustomer(
            businessId = businessId,
            customer = customer,
            onSuccess = { customerId ->
                _addCustomerState.value = UiState.Success(customerId)
                val normalizedPhone = customer.phone.filter(Char::isDigit)
                _existingCustomerPhones.value += normalizedPhone
            },
            onFailure = { exception ->
                _addCustomerState.value = UiState.Error(
                    exception.message ?: "Unable to add customer"
                )
            }
        )
    }
    fun resetAddCustomerState() {
        _addCustomerState.value = null
    }
}
