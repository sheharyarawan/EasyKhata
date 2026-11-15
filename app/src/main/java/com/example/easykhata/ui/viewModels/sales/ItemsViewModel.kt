package com.example.easykhata.ui.viewModels.sales
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.UnitItem
import com.example.easykhata.data.repositories.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository
) : ViewModel() {
    private val _units = MutableStateFlow(itemsRepository.getUnits())
    val units = _units.asStateFlow()
    private val _selectedUnit = MutableStateFlow(
        _units.value.firstOrNull() ?: UnitItem("Piece", "pcs")
    )
    val selectedUnit = _selectedUnit.asStateFlow()
    fun selectUnit(unit: UnitItem) {
        _selectedUnit.value = unit
        _units.value = _units.value.map {
            it.copy(isSelected = it.name == unit.name)
        }
    }
    private val _addItemState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val addItemState = _addItemState.asStateFlow()
    fun addItem(
        businessId: String,
        item: SaleItem
    ) {
        if (businessId.isBlank()) {
            _addItemState.value = UiState.Error("Business not found")
            return
        }
        if (item.name.isBlank()) {
            _addItemState.value = UiState.Error("Enter item name")
            return
        }
        if (item.price <= 0.0) {
            _addItemState.value = UiState.Error("Enter a valid price")
            return
        }
        _addItemState.value = UiState.Loading
        itemsRepository.addItem(
            businessId = businessId,
            item = item,
            onSuccess = { itemId ->
                _addItemState.value = UiState.Success(itemId)
            },
            onFailure = { exception ->
                _addItemState.value = UiState.Error(
                    exception.message ?: "Unable to add item"
                )
            }
        )
    }
    fun resetAddItemState() {
        _addItemState.value = UiState.Idle
    }
}