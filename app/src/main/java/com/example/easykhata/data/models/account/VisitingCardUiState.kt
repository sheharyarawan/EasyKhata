package com.example.easykhata.data.models.account
import androidx.annotation.ColorRes
import com.example.easykhata.R
data class VisitingCardUiState(
    val businessName: String = "",
    val businessType: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    @ColorRes val backgroundColor: Int = R.color.navy_blue
)