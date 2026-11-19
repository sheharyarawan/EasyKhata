package com.example.easykhata.ui.viewModels.account
import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.User
import com.example.easykhata.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _userState =
        MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState = _userState.asStateFlow()
    fun loadUserInfo() {
        _userState.value = UiState.Loading
        authRepository.getUserInfo(
            onSuccess = { user ->
                _userState.value = UiState.Success(user)
            },
            onFailure = { exception ->
                _userState.value = UiState.Error(
                    exception.message ?: "Unable to load user information"
                )
            }
        )
    }
    fun resetUserState() {
        _userState.value = UiState.Idle
    }
    override fun onCleared() {
        super.onCleared()
        authRepository.removeUserInfoListener()
    }
}
