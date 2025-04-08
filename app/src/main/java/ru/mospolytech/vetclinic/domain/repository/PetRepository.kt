package ru.mospolytech.vetclinic.domain.repository

import ru.mospolytech.vetclinic.data.util.NetworkError
import ru.mospolytech.vetclinic.data.util.Result
import ru.mospolytech.vetclinic.domain.model.Pet

interface PetRepository {
    suspend fun getAllPets(): Result<List<Pet>, NetworkError>
    suspend fun getPet(id: String): Result<Pet, NetworkError>
    suspend fun getSelectedPet(): Pet?
    suspend fun saveSelectedPet(pet: Pet)
}
