package ru.mospolytech.vetclinic.domain.repository

import ru.mospolytech.vetclinic.data.model.auth.SignInResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): SignInResult
}