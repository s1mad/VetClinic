package ru.mospolytech.vetclinic.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mospolytech.vetclinic.data.model.AuthorizationState
import ru.mospolytech.vetclinic.data.store.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

interface AuthManager {
    val authState: StateFlow<AuthorizationState>

    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    fun checkAndUpdateAuthState()
}

@Singleton
class AuthManagerImpl @Inject constructor(
    private val tokenStore: TokenStore
) : AuthManager {

    private val _authState = MutableStateFlow(AuthorizationState.IDLE)
    override val authState: StateFlow<AuthorizationState> get() = _authState.asStateFlow()

    override suspend fun getToken(): String? = tokenStore.get()

    override suspend fun saveToken(token: String) {
        tokenStore.save(token)
        _authState.update { AuthorizationState.AUTHORIZED }
    }

    override suspend fun clearToken() {
        tokenStore.clear()
        _authState.update { AuthorizationState.UNAUTHORIZED }
    }

    override fun checkAndUpdateAuthState() {
        _authState.update { AuthorizationState.LOADING }
        CoroutineScope(Dispatchers.IO).launch {
            val isAuthorized = getToken() != null
            _authState.update {
                if (isAuthorized) AuthorizationState.AUTHORIZED else AuthorizationState.UNAUTHORIZED
            }
        }
    }

    init {
        checkAndUpdateAuthState()
    }
}