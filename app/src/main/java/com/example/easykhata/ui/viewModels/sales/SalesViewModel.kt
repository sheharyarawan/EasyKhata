package com.example.easykhata.ui.viewModels.sales
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.PaymentMethod
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.SelectedSaleItem
import com.example.easykhata.data.repositories.SalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class SalesViewModel @Inject constructor(
    private val salesRepository: SalesRepository
) : ViewModel() {
    private val _addSaleState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val addSaleState = _addSaleState.asStateFlow()
    private val _saleDetailsState =
        MutableStateFlow<UiState<Sale>>(UiState.Idle)
    val saleDetailsState = _saleDetailsState.asStateFlow()
    private val _salesState = MutableStateFlow<UiState<List<Sale>>>(UiState.Idle)
    val salesState = _salesState.asStateFlow()
    private val _deleteSaleState =
        MutableStateFlow<UiState<String>>(UiState.Idle)

    val deleteSaleState = _deleteSaleState.asStateFlow()
    private val _selectedSaleItems =
        MutableStateFlow<List<SelectedSaleItem>>(emptyList())
    val selectedSaleItems = _selectedSaleItems.asStateFlow()
    fun addSale(
        businessId: String,
        sale: Sale
    ) {
        if (businessId.isBlank()) {
            _addSaleState.value =
                UiState.Error("Business not selected")
            return
        }
        if (sale.items.isEmpty()) {
            _addSaleState.value =
                UiState.Error("Please add at least one item")
            return
        }
        if (
            sale.paymentMethod == PaymentMethod.UDHAAR &&
            (sale.customerId.isBlank() || sale.customerName.isBlank())
        ) {
            _addSaleState.value =
                UiState.Error("Please select a customer for Udhaar")
            return
        }
        _addSaleState.value = UiState.Loading
        salesRepository.addSale(
            businessId = businessId,
            sale = sale,
            onSuccess = { saleId ->
                _addSaleState.value = UiState.Success(saleId)
            },
            onFailure = { exception ->
                _addSaleState.value = UiState.Error(
                    exception.message ?: "Failed to add sale"
                )
            }
        )
    }
    fun getSale(
        businessId: String,
        saleId: String
    ) {
        if (businessId.isBlank()) {
            _saleDetailsState.value =
                UiState.Error("Business not selected")
            return
        }
        if (saleId.isBlank()) {
            _saleDetailsState.value =
                UiState.Error("Invalid sale ID")
            return
        }
        _saleDetailsState.value = UiState.Loading
        salesRepository.getSale(
            businessId = businessId,
            saleId = saleId,
            onSuccess = { sale ->
                _saleDetailsState.value = UiState.Success(sale)
            },
            onFailure = { exception ->
                _saleDetailsState.value = UiState.Error(
                    exception.message ?: "Failed to load sale"
                )
            }
        )
    }
    fun increaseSaleItemQuantity(item: SaleItem) {
        val currentItems = _selectedSaleItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst {
            it.item.id == item.id
        }
        if (existingIndex != -1) {
            val existingItem = currentItems[existingIndex]

            currentItems[existingIndex] = existingItem.copy(
                quantity = existingItem.quantity + 1
            )
        } else {
            currentItems.add(
                SelectedSaleItem(
                    item = item,
                    quantity = 1
                )
            )
        }
        _selectedSaleItems.value = currentItems
    }
    fun decreaseSaleItemQuantity(item: SaleItem) {
        val currentItems = _selectedSaleItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst {
            it.item.id == item.id
        }
        if (existingIndex == -1) return
        val existingItem = currentItems[existingIndex]
        if (existingItem.quantity <= 1) {
            currentItems.removeAt(existingIndex)
        } else {
            currentItems[existingIndex] = existingItem.copy(
                quantity = existingItem.quantity - 1
            )
        }
        _selectedSaleItems.value = currentItems
    }
    fun observeSales(businessId: String) {
        _salesState.value = UiState.Loading
        salesRepository.observeSales(
            businessId = businessId,
            onSuccess = { sales ->
                _salesState.value = UiState.Success(sales)
            },
            onFailure = { exception ->
                _salesState.value = UiState.Error(
                    exception.message ?: "Failed to fetch sales"
                )
            }
        )
    }
    fun deleteSale(
        businessId: String,
        sale: Sale
    ) {
        if (businessId.isBlank()) {
            _deleteSaleState.value =
                UiState.Error("Business not selected")
            return
        }
        if (sale.id.isBlank()) {
            _deleteSaleState.value =
                UiState.Error("Invalid sale ID")
            return
        }
        _deleteSaleState.value = UiState.Loading
        salesRepository.deleteSale(
            businessId = businessId,
            sale = sale,
            onSuccess = {
                _deleteSaleState.value =
                    UiState.Success(sale.id)
            },
            onFailure = { exception ->
                _deleteSaleState.value =
                    UiState.Error(
                        exception.message ?: "Failed to delete sale"
                    )
            }
        )
    }
    fun resetDeleteSaleState() {
        _deleteSaleState.value = UiState.Idle
    }
    fun removeSalesListener() {
        salesRepository.removeSalesListener()
    }
    fun getSelectedQuantity(itemId: String): Int {
        return _selectedSaleItems.value
            .find { it.item.id == itemId }
            ?.quantity ?: 0
    }
    fun clearSelectedSaleItems() {
        _selectedSaleItems.value = emptyList()
    }
    fun resetAddSaleState() {
        _addSaleState.value = UiState.Idle
    }
    fun resetSaleDetailsState() {
        _saleDetailsState.value = UiState.Idle
    }
    override fun onCleared() {
        super.onCleared()
        removeSalesListener()
    }
}
