package com.example.easykhata.di

import com.example.easykhata.data.repositories.AuthRepository
import com.example.easykhata.data.repositories.AuthRepositoryImpl
import com.example.easykhata.data.repositories.BusinessRepository
import com.example.easykhata.data.repositories.BusinessRepositoryImpl
import com.example.easykhata.data.repositories.CustomerRepository
import com.example.easykhata.data.repositories.CustomerRepositoryImpl
import com.example.easykhata.data.repositories.ExpenseRepository
import com.example.easykhata.data.repositories.ExpenseRepositoryImpl
import com.example.easykhata.data.repositories.PhoneContactsRepository
import com.example.easykhata.data.repositories.PhoneContactsRepositoryImpl
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
    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository
    @Binds
    @Singleton
    abstract fun bindPhoneContactsRepository(
        phoneContactsRepositoryImpl: PhoneContactsRepositoryImpl
    ): PhoneContactsRepository
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

}