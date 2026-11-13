package com.example.easykhata.data.dataSource

import android.content.ContentResolver
import android.provider.ContactsContract
import com.example.easykhata.data.models.customers.Contact
import javax.inject.Inject
class PhoneContactsDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.use {
            val idIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val nameIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val numberIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex) ?: continue
                contacts.add(
                    Contact(
                        id = id,
                        name = name,
                        phoneNumber = number
                    )
                )
            }
        }
        return contacts
    }
}