package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.UnitItem

interface ItemsRepository {
    fun addItem(
        businessId: String,
        item: SaleItem,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getUnits(): List<UnitItem>
    fun observeItems(
        businessId: String,
        onSuccess: (List<SaleItem>) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun removeItemsListener()
    fun getItemById(
        businessId: String,
        itemId: String,
        onSuccess: (SaleItem) -> Unit,
        onFailure: (Exception) -> Unit
    )

    fun updateItem(
        businessId: String,
        item: SaleItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}