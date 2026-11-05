package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.auth.Business
import com.example.easykhata.data.models.auth.User
interface BusinessRepository {
    fun createUserWithBusiness(
        user: User,
        business: Business,
        onSuccess: (String) -> Unit,
        onException: (Exception) -> Unit
    )
    fun getKarobarTypes(): List<String>
}