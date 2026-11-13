package com.example.easykhata.data.models.account
import com.example.easykhata.data.models.ReminderStatus
import com.google.firebase.Timestamp
data class WasooliReminder(
    val id: String = "",
    val customerId: String = "",
    val reminderDate: Timestamp? = null,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
