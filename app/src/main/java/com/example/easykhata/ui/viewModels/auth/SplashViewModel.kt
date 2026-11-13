package com.example.easykhata.ui.viewModels.auth

import androidx.lifecycle.ViewModel
import com.example.easykhata.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn()
    }
    suspend fun hasBusiness(): Boolean {
        val uid = authRepository.getCurrentUserUid() ?: return false
        return authRepository.hasBusiness(uid)
    }
}