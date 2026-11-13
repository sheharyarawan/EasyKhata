package com.example.easykhata.data.models.account
import com.example.easykhata.data.models.customers.CustomerWithBalance
data class WasooliCustomerItem(
    val customerWithBalance: CustomerWithBalance,
    val reminder: WasooliReminder
)