package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.customers.Customer
interface CustomerRepository {
    fun addCustomer(
        businessId: String,
        customer: Customer,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getCustomers(
        businessId: String,
        onSuccess: (List<Customer>) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getCustomerById(
        businessId: String,
        customerId: String,
        onSuccess: (Customer) -> Unit,
        onFailure: (Exception) -> Unit
    )
}