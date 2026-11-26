package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.account.LocationSearchResult
import com.example.easykhata.data.models.account.SelectedLocation
import com.example.easykhata.data.remote.PhotonApi
import com.example.easykhata.data.remote.PhotonProperties
import javax.inject.Inject
class LocationRepositoryImpl @Inject constructor(
    private val photonApi: PhotonApi
) : LocationRepository {
    override suspend fun searchLocations(
        query: String
    ): List<LocationSearchResult> {
        return photonApi.searchLocation(query)
            .features
            .mapNotNull { feature ->
                val coordinates = feature.geometry.coordinates
                if (coordinates.size < 2) return@mapNotNull null
                val address = feature.properties.toFormattedAddress()
                LocationSearchResult(
                    name = feature.properties.name ?: address,
                    address = address,
                    longitude = coordinates[0],
                    latitude = coordinates[1]
                )
            }
    }
    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): SelectedLocation {
        val response = photonApi.reverseGeocode(latitude, longitude)
        val address = response.features
            .firstOrNull()
            ?.properties
            ?.toFormattedAddress()
            ?: "Unknown location"
        return SelectedLocation(
            latitude = latitude,
            longitude = longitude,
            address = address
        )
    }
    private fun PhotonProperties.toFormattedAddress(): String {
        val streetAddress = listOfNotNull(
            housenumber,
            street
        ).joinToString(" ").takeIf { it.isNotBlank() }
        return listOfNotNull(
            name,
            streetAddress,
            postcode,
            district,
            city,
            state,
            country
        )
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(", ")
            .ifBlank { "Unknown location" }
    }
}
