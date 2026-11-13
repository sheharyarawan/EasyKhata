package com.example.easykhata.ui.viewModels.customer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.repositories.CustomerRepository
import com.example.easykhata.data.repositories.PhoneContactsRepository
import com.example.easykhata.utils.PhoneNumberUtils
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
                val seenPhones = mutableSetOf<String>()
                val uniqueContacts = contacts
                    .map { contact ->
                        contact.copy(
                            phoneNumber = PhoneNumberUtils.normalize(contact.phoneNumber)
                        )
                    }
                    .filter { contact ->
                        contact.phoneNumber.isBlank() ||
                                seenPhones.add(contact.phoneNumber)
                    }
                _contactsState.value = UiState.Success(uniqueContacts)
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
                    .map {
                        PhoneNumberUtils.normalize(it.phone)
                    }
                    .filter { it.isNotBlank() }
                    .toSet()
            },
            onFailure = {
                _existingCustomerPhones.value = emptySet()
            }
        )
    }
    fun addCustomer(
        businessId: String,
        customer: Customer,
        isFromContact: Boolean = false
    ) {
        if (businessId.isBlank()) {
            _addCustomerState.value = UiState.Error(
                "Please select a business first"
            )
            return
        }
        val phone = if (isFromContact) {
            PhoneNumberUtils.normalize(customer.phone)
        } else {
            customer.phone.trim()
        }
        if (phone.isNotEmpty() &&
            !PhoneNumberUtils.isValidLocalNumber(phone)
        ) {
            _addCustomerState.value = UiState.Error(
                "Durust phone number darj karein"
            )
            return
        }
        val customerToSave = customer.copy(
            phone = phone
        )
        _addCustomerState.value = UiState.Loading
        customerRepository.addCustomer(
            businessId = businessId,
            customer = customerToSave,
            onSuccess = { customerId ->
                _addCustomerState.value = UiState.Success(customerId)
                if (phone.isNotBlank()) {
                    _existingCustomerPhones.value += phone
                }
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