package com.example.easykhata.di
import com.example.easykhata.data.remote.PhotonApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object PhotonNetworkModule {
    @Provides
    @Singleton
    fun providePhotonOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "EasyKhata/1.0 (Android; com.example.easykhata)"
                    )
                    .header("Accept", "application/json")
                    .build()

                chain.proceed(request)
            }
            .build()
    }
    @Provides
    @Singleton
    @Named("photon")
    fun providePhotonRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://photon.komoot.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun providePhotonApi(
        @Named("photon") retrofit: Retrofit
    ): PhotonApi {
        return retrofit.create(PhotonApi::class.java)
    }
}
