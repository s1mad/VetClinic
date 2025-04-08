package ru.mospolytech.vetclinic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: Int,
    val name: String,
    val age: Int,
    val animalType: String,
    val behavior: String,
    val condition: String,
    val gender: String,
    val researchStatus: String,
    val weight: Int
)