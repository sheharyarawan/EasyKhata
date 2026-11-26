package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.account.LocationSearchResult
import com.example.easykhata.data.models.account.SelectedLocation
interface LocationRepository {
    suspend fun searchLocations(
        query: String
    ): List<LocationSearchResult>
    suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): SelectedLocation
}