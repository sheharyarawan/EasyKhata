package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.sales.Sale
interface SalesRepository {
    fun addSale(
        businessId: String,
        sale: Sale,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
}