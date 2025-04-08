package ru.mospolytech.vetclinic.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.mospolytech.vetclinic.data.constant.StoreConstants
import ru.mospolytech.vetclinic.domain.model.Pet
import javax.inject.Inject

interface PetStore {
    suspend fun saveSelected(pet: Pet)
    suspend fun getSelected(): Pet?
    suspend fun clearSelected()
}

class PetStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : PetStore {

    private val PREF_KEY = stringPreferencesKey(StoreConstants.SELECTED_PET_KEY)

    override suspend fun saveSelected(pet: Pet): Unit = withContext(Dispatchers.IO) {
        val petJson = json.encodeToString(pet)
        dataStore.edit { prefs ->
            prefs[PREF_KEY] = petJson
        }
    }

    override suspend fun getSelected(): Pet? = withContext(Dispatchers.IO) {
        val petJson = dataStore.data.firstOrNull()?.get(PREF_KEY) ?: return@withContext null
        return@withContext json.decodeFromString(petJson)
    }


    override suspend fun clearSelected(): Unit = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(PREF_KEY)
        }
    }

}