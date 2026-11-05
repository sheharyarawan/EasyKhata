package com.example.easykhata.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.Business
import com.example.easykhata.data.models.auth.User
import com.example.easykhata.data.repositories.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) : ViewModel() {
    private val _registrationState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val registrationState = _registrationState.asStateFlow()
    private val _selectedKarobarType = MutableStateFlow("")
    val selectedKarobarType = _selectedKarobarType.asStateFlow()
    fun createUserWithBusiness(
        user: User,
        business: Business
    ) {
        _registrationState.value = UiState.Loading
        businessRepository.createUserWithBusiness(
            user = user,
            business = business,
            onSuccess = { businessId ->
                _registrationState.value =
                    UiState.Success(businessId)
            },
            onException = { exception ->
                _registrationState.value = UiState.Error(
                    exception.message ?: "Unable to create account"
                )
            }
        )
    }
    fun getKarobarTypes(): List<String> {
        return businessRepository.getKarobarTypes()
    }
    fun setSelectedKarobarType(type: String) {
        _selectedKarobarType.value = type
    }
}
