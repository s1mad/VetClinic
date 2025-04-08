package ru.mospolytech.vetclinic.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.mospolytech.vetclinic.data.constant.StoreConstants
import ru.mospolytech.vetclinic.data.store.PetStore
import ru.mospolytech.vetclinic.data.store.PetStoreImpl
import ru.mospolytech.vetclinic.data.store.TokenStore
import ru.mospolytech.vetclinic.data.store.TokenStoreImpl
import ru.mospolytech.vetclinic.data.util.AuthManager
import ru.mospolytech.vetclinic.data.util.AuthManagerImpl
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = StoreConstants.DATA_STORE_NAME)

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

}

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreBindsModule {

    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: TokenStoreImpl): TokenStore

    @Binds
    @Singleton
    abstract fun bindAuthManager(impl: AuthManagerImpl): AuthManager

    @Binds
    @Singleton
    abstract fun bindPetStore(impl: PetStoreImpl): PetStore

}

