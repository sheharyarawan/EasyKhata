package com.example.easykhata.data.models.auth

import com.google.firebase.auth.PhoneAuthCredential
data class PhoneAuthData(
    val verificationId: String? = null,
    val credential: PhoneAuthCredential? = null
)