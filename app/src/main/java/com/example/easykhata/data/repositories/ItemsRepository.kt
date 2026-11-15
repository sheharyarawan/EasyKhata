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
}