package com.example.easykhata.data.models.auth
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class User(
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val activeBusinessId: String = "",
    val createdAt: Long = System.currentTimeMillis()
): Parcelable