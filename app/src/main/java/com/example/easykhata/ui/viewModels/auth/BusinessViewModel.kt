package com.example.easykhata.ui.viewModels.auth
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
    private val _businessId = MutableStateFlow<String?>(null)
    val businessId = _businessId.asStateFlow()
    private val _newBusinessState =
        MutableStateFlow<UiState<String>>(UiState.Idle)

    val newBusinessState = _newBusinessState.asStateFlow()
    private val _businessesState =
        MutableStateFlow<UiState<List<Business>>>(UiState.Idle)

    val businessesState = _businessesState.asStateFlow()
    private val _switchBusinessState =
        MutableStateFlow<UiState<String>>(UiState.Idle)

    val switchBusinessState = _switchBusinessState.asStateFlow()
    fun createUserWithBusiness(
        user: User,
        business: Business
    ) {
        _registrationState.value = UiState.Loading
        businessRepository.createUserWithBusiness(
            user = user,
            business = business,
            onSuccess = { businessId ->
                setBusinessId(businessId)
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
    fun loadActiveBusinessId(onComplete: () -> Unit) {
        businessRepository.getActiveBusinessId(
            onSuccess = { id ->
                setBusinessId(id)
                onComplete()
            },
            onFailure = { exception ->
                _businessId.value = null
                onComplete()
            }
        )
    }
    fun createNewBusiness(businessName: String) {
        if (businessName.isBlank()) {
            _newBusinessState.value =
                UiState.Error("Please enter karobar name")
            return
        }
        _newBusinessState.value = UiState.Loading
        businessRepository.createBusiness(
            businessName = businessName.trim(),
            onSuccess = { id ->
                setBusinessId(id)
                loadUserBusinesses()
                _newBusinessState.value =
                    UiState.Success(id)
            },
            onFailure = { exception ->
                _newBusinessState.value =
                    UiState.Error(
                        exception.message ?: "Unable to create business"
                    )
            }
        )
    }
    fun loadUserBusinesses() {
        _businessesState.value = UiState.Loading
        businessRepository.getUserBusinesses(
            onSuccess = { businesses ->
                _businessesState.value =
                    UiState.Success(businesses)
            },
            onFailure = { exception ->
                _businessesState.value =
                    UiState.Error(
                        exception.message ?: "Unable to load businesses"
                    )
            }
        )
    }
    fun switchBusiness(businessId: String) {
        if (businessId.isBlank()) {
            _switchBusinessState.value =
                UiState.Error("Invalid business")
            return
        }
        _switchBusinessState.value = UiState.Loading
        businessRepository.switchBusiness(
            businessId = businessId,
            onSuccess = {
                setBusinessId(businessId)
                _switchBusinessState.value =
                    UiState.Success(businessId)
            },
            onFailure = { exception ->
                _switchBusinessState.value =
                    UiState.Error(
                        exception.message ?: "Unable to switch business"
                    )
            }
        )
    }
    fun resetNewBusinessState() {
        _newBusinessState.value = UiState.Idle
    }
    fun getKarobarTypes(): List<String> {
        return businessRepository.getKarobarTypes()
    }
    fun setSelectedKarobarType(type: String) {
        _selectedKarobarType.value = type
    }
    fun setBusinessId(id: String) {
        _businessId.value = id
    }
    fun resetBusinessesState() {
        _businessesState.value = UiState.Idle
    }
    fun resetSwitchBusinessState() {
        _switchBusinessState.value = UiState.Idle
    }
}