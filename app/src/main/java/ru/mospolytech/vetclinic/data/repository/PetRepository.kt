package ru.mospolytech.vetclinic.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mospolytech.vetclinic.data.api.PetApi
import ru.mospolytech.vetclinic.data.model.pet.toPet
import ru.mospolytech.vetclinic.data.store.PetStore
import ru.mospolytech.vetclinic.data.util.AuthManager
import ru.mospolytech.vetclinic.data.util.NetworkError
import ru.mospolytech.vetclinic.data.util.Result
import ru.mospolytech.vetclinic.data.util.executeRequest
import ru.mospolytech.vetclinic.data.util.map
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.domain.repository.PetRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val authManager: AuthManager,
    private val petApi: PetApi,
    private val petStore: PetStore
) : PetRepository {

    override suspend fun getAllPets(): Result<List<Pet>, NetworkError> = withContext(Dispatchers.IO) {
        val userId = authManager.getUserIdFromToken()
        executeRequest {
            petApi.getAllPets(ownerId = userId)
        }.map { list -> list.map { it.toPet() } }
    }

    override suspend fun getPet(id: String): Result<Pet, NetworkError> = withContext(Dispatchers.IO) {
        executeRequest {
            petApi.getPet(id)
        }.map { it.toPet() }
    }

    override suspend fun getSelectedPet(): Pet? = petStore.getSelected()

    override suspend fun saveSelectedPet(pet: Pet) = petStore.saveSelected(pet)
}