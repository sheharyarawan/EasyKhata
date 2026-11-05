package com.example.easykhata.ui.viewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.PhoneAuthData
import com.example.easykhata.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<UiState<PhoneAuthData>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _businessState =
        MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val businessState = _businessState.asStateFlow()
    private val _resendOtpState =
        MutableStateFlow<UiState<PhoneAuthData>>(UiState.Idle)
    val resendOtpState = _resendOtpState.asStateFlow()
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
    fun resendOtp(
        phoneNumber: String,
        activity: Activity
    ) {
        _resendOtpState.value = UiState.Loading
        authRepository.sendOtp(
            phoneNumber = phoneNumber,
            activity = activity,

            onCodeSent = { verificationId ->
                _resendOtpState.value = UiState.Success(
                    PhoneAuthData(
                        verificationId = verificationId
                    )
                )
            },
            onVerificationCompleted = { credential ->
                _resendOtpState.value = UiState.Success(
                    PhoneAuthData(
                        credential = credential
                    )
                )
            },
            onError = { exception ->
                _resendOtpState.value = UiState.Error(
                    exception.message ?: "OTP dobara send nahi ho saka"
                )
            }
        )
    }
    fun verifyOtp(
        verificationId: String,
        otp: String
    ) {
        _uiState.value = UiState.Loading
        authRepository.verifyOtp(
            verificationId = verificationId,
            otp = otp,
            onSuccess = {
                _uiState.value = UiState.Success(
                    PhoneAuthData()
                )
            },
            onError = { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "OTP verification failed"
                )
            }
        )
    }
    fun checkExistingBusiness() {
        viewModelScope.launch {
            _businessState.value = UiState.Loading
            try {
                val uid = authRepository.getCurrentUserUid()
                if (uid == null) {
                    _businessState.value =
                        UiState.Error("User not authenticated")
                    return@launch
                }
                val hasBusiness = authRepository.hasBusiness(uid)
                _businessState.value =
                    UiState.Success(hasBusiness)
            } catch (e: Exception) {
                _businessState.value = UiState.Error(
                    e.message ?: "Unable to check business details"
                )
            }
        }
    }
}