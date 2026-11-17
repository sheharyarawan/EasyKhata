package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.sales.Sale
interface SalesRepository {
    fun addSale(
        businessId: String,
        sale: Sale,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getSale(
        businessId: String,
        saleId: String,
        onSuccess: (Sale) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun observeSales(
        businessId: String,
        onSuccess: (List<Sale>) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun removeSalesListener()
}