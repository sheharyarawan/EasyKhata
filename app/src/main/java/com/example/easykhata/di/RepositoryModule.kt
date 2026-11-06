package com.example.easykhata.di

import com.example.easykhata.data.repositories.AuthRepository
import com.example.easykhata.data.repositories.AuthRepositoryImpl
import com.example.easykhata.data.repositories.BusinessRepository
import com.example.easykhata.data.repositories.BusinessRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    @Binds
    @Singleton
    abstract fun bindBusinessRepository(
        businessRepositoryImpl: BusinessRepositoryImpl
    ): BusinessRepository
}