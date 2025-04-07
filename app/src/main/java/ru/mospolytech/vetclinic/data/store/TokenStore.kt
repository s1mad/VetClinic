package ru.mospolytech.vetclinic.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import ru.mospolytech.vetclinic.data.constant.StoreConstants
import javax.inject.Inject

interface TokenStore {
    suspend fun save(token: String)
    suspend fun get(): String?
    suspend fun clear()
}

class TokenStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenStore {

    private val PREF_KEY = stringPreferencesKey(StoreConstants.AUTH_TOKEN_KEY)

    override suspend fun save(token: String): Unit = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs[PREF_KEY] = token
        }
    }

    override suspend fun get(): String? = withContext(Dispatchers.IO) {
        dataStore.data.firstOrNull()?.get(PREF_KEY)
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(PREF_KEY)
        }
    }
}