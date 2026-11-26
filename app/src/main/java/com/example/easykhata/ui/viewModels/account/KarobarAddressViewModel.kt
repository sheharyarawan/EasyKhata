package com.example.easykhata.ui.viewModels.account
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.LocationSearchResult
import com.example.easykhata.data.models.account.SelectedLocation
import com.example.easykhata.data.repositories.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject
@HiltViewModel
class KarobarAddressViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _searchState =
        MutableStateFlow<UiState<List<LocationSearchResult>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<LocationSearchResult>>> =
        _searchState.asStateFlow()
    private val _selectedLocation =
        MutableStateFlow<UiState<SelectedLocation>>(UiState.Idle)
    val selectedLocation: StateFlow<UiState<SelectedLocation>> =
        _selectedLocation.asStateFlow()
    private var searchJob: Job? = null
    fun onLocationSelected(location: SelectedLocation) {
        _selectedLocation.value = UiState.Success(location)
    }
    fun searchLocations(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.value = UiState.Idle
            return
        }
        searchJob = viewModelScope.launch {
            delay(1000.milliseconds)
            _searchState.value = UiState.Loading
            try {
                val results = locationRepository.searchLocations(query.trim())
                _searchState.value = UiState.Success(results)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _searchState.value = UiState.Error(
                    e.message ?: "Unable to search locations"
                )
            }
        }
    }
    fun selectLocationFromCoordinates(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _selectedLocation.value = UiState.Loading
            try {
                val location = locationRepository.getAddressFromCoordinates(
                    latitude = latitude,
                    longitude = longitude
                )
                _selectedLocation.value = UiState.Success(location)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _selectedLocation.value = UiState.Error(
                    e.message ?: "Unable to get location address"
                )
            }
        }
    }
}
