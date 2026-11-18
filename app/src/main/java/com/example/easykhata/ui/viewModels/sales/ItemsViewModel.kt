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
    private val _getItemState =
        MutableStateFlow<UiState<SaleItem>>(UiState.Idle)
    val getItemState = _getItemState.asStateFlow()
    private val _updateItemState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateItemState = _updateItemState.asStateFlow()
    private val _itemsState =
        MutableStateFlow<UiState<List<SaleItem>>>(UiState.Loading)
    val itemsState = _itemsState.asStateFlow()
    private val _deleteItemState =
        MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteItemState = _deleteItemState.asStateFlow()
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
    fun getItemById(
        businessId: String,
        itemId: String
    ) {
        if (businessId.isBlank()) {
            _getItemState.value = UiState.Error("Business not found")
            return
        }
        if (itemId.isBlank()) {
            _getItemState.value = UiState.Error("Invalid item")
            return
        }
        _getItemState.value = UiState.Loading
        itemsRepository.getItemById(
            businessId = businessId,
            itemId = itemId,
            onSuccess = { item ->
                _getItemState.value = UiState.Success(item)
            },
            onFailure = { exception ->
                _getItemState.value = UiState.Error(
                    exception.message ?: "Unable to load item"
                )
            }
        )
    }
    fun updateItem(
        businessId: String,
        item: SaleItem
    ) {
        if (businessId.isBlank()) {
            _updateItemState.value = UiState.Error("Business not found")
            return
        }
        if (item.id.isBlank()) {
            _updateItemState.value = UiState.Error("Invalid item")
            return
        }
        if (item.name.isBlank()) {
            _updateItemState.value = UiState.Error("Enter item name")
            return
        }
        if (item.price <= 0.0) {
            _updateItemState.value = UiState.Error("Enter a valid price")
            return
        }
        _updateItemState.value = UiState.Loading
        itemsRepository.updateItem(
            businessId = businessId,
            item = item,
            onSuccess = {
                _updateItemState.value = UiState.Success(Unit)
            },
            onFailure = { exception ->
                _updateItemState.value = UiState.Error(
                    exception.message ?: "Unable to update item"
                )
            }
        )
    }
    fun observeItems(businessId: String) {
        if (businessId.isBlank()) {
            _itemsState.value = UiState.Error("Business not found")
            return
        }
        _itemsState.value = UiState.Loading
        itemsRepository.observeItems(
            businessId = businessId,
            onSuccess = { items ->
                _itemsState.value = UiState.Success(items)
            },
            onFailure = { exception ->
                _itemsState.value = UiState.Error(
                    exception.message ?: "Unable to load items"
                )
            }
        )
    }
    fun deleteItem(
        businessId: String,
        itemId: String
    ) {
        if (businessId.isBlank()) {
            _deleteItemState.value = UiState.Error("Business not found")
            return
        }
        if (itemId.isBlank()) {
            _deleteItemState.value = UiState.Error("Invalid item")
            return
        }
        _deleteItemState.value = UiState.Loading
        itemsRepository.deleteItem(
            businessId = businessId,
            itemId = itemId,
            onSuccess = {
                _deleteItemState.value = UiState.Success(Unit)
            },
            onFailure = { exception ->
                _deleteItemState.value = UiState.Error(
                    exception.message ?: "Unable to delete item"
                )
            }
        )
    }
    fun removeItemsListener() {
        itemsRepository.removeItemsListener()
    }
    fun resetAddItemState() {
        _addItemState.value = UiState.Idle
    }
    fun resetGetItemState() {
        _getItemState.value = UiState.Idle
    }
    fun resetUpdateItemState() {
        _updateItemState.value = UiState.Idle
    }
    fun resetDeleteItemState() {
        _deleteItemState.value = UiState.Idle
    }
    override fun onCleared() {
        removeItemsListener()
        super.onCleared()
    }
}