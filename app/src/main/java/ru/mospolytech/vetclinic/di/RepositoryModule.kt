package ru.mospolytech.vetclinic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mospolytech.vetclinic.data.repository.AuthRepositoryImpl
import ru.mospolytech.vetclinic.data.repository.PetRepositoryImpl
import ru.mospolytech.vetclinic.domain.repository.AuthRepository
import ru.mospolytech.vetclinic.domain.repository.PetRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPetRepository(impl: PetRepositoryImpl): PetRepository

}