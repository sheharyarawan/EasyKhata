package com.example.easykhata.data.repositories
import android.app.Activity
import com.google.firebase.auth.PhoneAuthCredential

interface AuthRepository {
    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onError: (Exception) -> Unit
    )
}