package com.example.easykhata.ui.viewModels.customerViewModel
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.repositories.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    private val _customerState =
        MutableStateFlow<UiState<Customer>>(UiState.Loading)
    val customerState = _customerState.asStateFlow()
    fun loadCustomer(businessId: String, customerId: String) {
        _customerState.value = UiState.Loading
        customerRepository.getCustomerById(
            businessId = businessId,
            customerId = customerId,
            onSuccess = { customer ->
                _customerState.value = UiState.Success(customer)
            },
            onFailure = { exception ->
                _customerState.value = UiState.Error(
                    exception.message ?: "Unable to load customer"
                )
            }
        )
    }
    fun updateCustomer(businessId: String, customer: Customer) {
        if (businessId.isBlank() || customer.id.isBlank()) {
            _customerState.value =
                UiState.Error("Business or customer not selected")
            return
        }
        if (customer.name.isBlank()) {
            _customerState.value =
                UiState.Error("Name is required")
            return
        }
        _customerState.value = UiState.Loading
        customerRepository.updateCustomer(
            businessId = businessId,
            customer = customer,
            onSuccess = {
                _customerState.value = UiState.Success(customer)
            },
            onFailure = { exception ->
                _customerState.value = UiState.Error(
                    exception.message ?: "Unable to update customer"
                )
            }
        )
    }
}