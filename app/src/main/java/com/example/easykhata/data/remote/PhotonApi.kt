package com.example.easykhata.data.remote
import retrofit2.http.GET
import retrofit2.http.Query
interface PhotonApi {
    @GET("api")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") language: String = "en",
        @Query("lat") latitude: Double? = null,
        @Query("lon") longitude: Double? = null
    ): PhotonResponse
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String = "en"
    ): PhotonResponse
}
