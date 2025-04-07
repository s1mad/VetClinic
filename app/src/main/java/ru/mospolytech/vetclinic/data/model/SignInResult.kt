package ru.mospolytech.vetclinic.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(
    val success: Boolean,
    val message: String? = null
)
