package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.customers.Contact
interface PhoneContactsRepository {
    suspend fun getPhoneContacts(): List<Contact>
}