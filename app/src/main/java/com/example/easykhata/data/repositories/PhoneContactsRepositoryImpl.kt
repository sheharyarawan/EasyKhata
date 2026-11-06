package com.example.easykhata.data.repositories

import com.example.easykhata.data.dataSource.PhoneContactsDataSource
import com.example.easykhata.data.models.customers.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
class PhoneContactsRepositoryImpl @Inject constructor(
    private val phoneContactsDataSource: PhoneContactsDataSource
) : PhoneContactsRepository {
    override suspend fun getPhoneContacts(): List<Contact> =
        withContext(Dispatchers.IO) {
            phoneContactsDataSource.getContacts()
        }
}