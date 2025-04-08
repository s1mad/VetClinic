package ru.mospolytech.vetclinic.domain.usecase

import ru.mospolytech.vetclinic.data.util.AuthManager
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val manager: AuthManager
) {
    suspend operator fun invoke() {
        manager.clearToken()
    }
}