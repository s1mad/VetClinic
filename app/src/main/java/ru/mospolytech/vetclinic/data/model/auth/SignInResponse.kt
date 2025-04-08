package ru.mospolytech.vetclinic.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val token: String? = null,
    val message: String? = null,
)
