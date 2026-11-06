package com.example.easykhata.ui.viewModels.customerViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.data.repositories.PhoneContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val phoneContactsRepository: PhoneContactsRepository
) : ViewModel() {
    private val _contactsState =
        MutableStateFlow<UiState<List<Contact>>>(UiState.Loading)
    val contactsState = _contactsState.asStateFlow()
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
}