package com.example.easykhata.ui.viewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.PhoneAuthData
import com.example.easykhata.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<UiState<PhoneAuthData>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()
    fun sendOtp(
        phoneNumber: String,
        activity: Activity
    ) {
        _uiState.value = UiState.Loading
        authRepository.sendOtp(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = { verificationId ->
                _uiState.value = UiState.Success(
                    PhoneAuthData(
                        verificationId = verificationId
                    )
                )
            },
            onVerificationCompleted = { credential ->
                _uiState.value = UiState.Success(
                    PhoneAuthData(
                        credential = credential
                    )
                )
            },
            onError = { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Request fail hogi, dubaara koshish karein"
                )
            }
        )
    }
}