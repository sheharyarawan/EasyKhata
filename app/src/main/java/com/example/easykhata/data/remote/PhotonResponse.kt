package com.example.easykhata.data.remote
import com.google.gson.annotations.SerializedName
data class PhotonResponse(
    @SerializedName("features")
    val features: List<PhotonFeature> = emptyList()
)
data class PhotonFeature(
    @SerializedName("geometry")
    val geometry: PhotonGeometry,
    @SerializedName("properties")
    val properties: PhotonProperties
)
data class PhotonGeometry(
    @SerializedName("coordinates")
    val coordinates: List<Double>
)
data class PhotonProperties(
    val name: String? = null,
    val street: String? = null,
    val housenumber: String? = null,
    val postcode: String? = null,
    val district: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null
)
