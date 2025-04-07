package ru.mospolytech.vetclinic.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)
