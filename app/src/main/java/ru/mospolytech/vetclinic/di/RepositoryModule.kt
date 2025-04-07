package ru.mospolytech.vetclinic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mospolytech.vetclinic.data.api.AuthApi
import ru.mospolytech.vetclinic.data.repository.AuthRepository
import ru.mospolytech.vetclinic.data.repository.AuthRepositoryImpl
import ru.mospolytech.vetclinic.data.util.AuthManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi, authManager: AuthManager): AuthRepository = AuthRepositoryImpl(
        authApi = authApi,
        authManager = authManager
    )

}