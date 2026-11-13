package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.account.WasooliReminder
interface WasooliRepository {
    fun addReminder(
        businessId: String,
        reminder: WasooliReminder,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun observeReminders(
        businessId: String,
        onSuccess: (List<WasooliReminder>) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun removeRemindersListener()
    fun deleteReminder(
        businessId: String,
        customerId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}