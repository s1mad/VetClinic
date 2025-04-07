package ru.mospolytech.vetclinic.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.mospolytech.vetclinic.data.api.AuthApi
import ru.mospolytech.vetclinic.data.model.SignInRequest
import ru.mospolytech.vetclinic.data.model.SignInResult
import ru.mospolytech.vetclinic.data.util.AuthManager
import javax.inject.Inject

interface AuthRepository {
    suspend fun signIn(email: String, password: String): SignInResult
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authManager: AuthManager
) : AuthRepository {

    override suspend fun signIn(
        email: String, password: String
    ): SignInResult = withContext(Dispatchers.IO) {

        val request = SignInRequest(email.trim(), password.trim())

        validateSigInRequest(request)?.let { message ->
            return@withContext SignInResult(
                success = false,
                message = message
            )
        }

        val response = try {
            authApi.signIn(request).execute()
        } catch (e: Exception) {
            return@withContext SignInResult(
                success = false,
                message = e.message
            )
        }

        if (response.isSuccessful) {
            return@withContext response.body()?.token?.let {
                authManager.saveToken(it)
                SignInResult(
                    success = true
                )
            } ?: SignInResult(
                success = false,
                message = response.body()?.message ?: "Авторизация не удалась"
            )
        } else {
            val message = response.errorBody()?.string()?.let { json ->
                runCatching { JSONObject(json).getString("message") }.getOrNull()
            }

            return@withContext SignInResult(
                success = false,
                message = message
            )
        }
    }

    private fun validateSigInRequest(signInRequest: SignInRequest): String? =
        with(signInRequest) {
            if (email.isBlank()) {
                return "Email не может быть пустым"
            }

            if (password.isBlank()) {
                return "Пароль не может быть пустым"
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return "Некорректный формат email"
            }
            return null
        }
}