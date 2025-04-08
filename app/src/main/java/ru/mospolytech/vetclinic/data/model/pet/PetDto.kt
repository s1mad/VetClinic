package ru.mospolytech.vetclinic.data.model.pet

import kotlinx.serialization.Serializable
import ru.mospolytech.vetclinic.domain.model.Pet

@Serializable
data class PetDto(
    val owner_id: Int,
    val vet_id: Int,
    val pet_info: PetInfo
)

@Serializable
data class PetInfo(
    val id: Int,
    val name: String,
    val age: Int,
    val animal_type: String,
    val behavior: String,
    val condition: String,
    val gender: String,
    val research_status: String,
    val weight: Int
)

fun PetDto.toPet(): Pet = pet_info.toPet()

fun PetInfo.toPet(): Pet = Pet(
    id = id,
    name = name,
    age = age,
    animalType = animal_type,
    behavior = behavior,
    condition = condition,
    gender = gender,
    researchStatus = research_status,
    weight = weight
)