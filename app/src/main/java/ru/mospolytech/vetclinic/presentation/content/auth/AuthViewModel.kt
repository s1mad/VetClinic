package ru.mospolytech.vetclinic.presentation.content.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.mospolytech.vetclinic.domain.repository.AuthRepository
import javax.inject.Inject

abstract class AuthViewModel : ViewModel() {

    abstract val state: StateFlow<State>
    abstract fun onEvent(event: Event)

    @Serializable
    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    @Serializable
    sealed class Event {
        data class EmailChanged(val new: String) : Event()
        data class PasswordChanged(val new: String) : Event()
        data object AuthButtonClick : Event()
    }
}

@HiltViewModel
class AuthViewModelImpl @Inject constructor(
    private val repo: AuthRepository
) : AuthViewModel() {

    private val _state = MutableStateFlow(State())
    override val state get() = _state.asStateFlow()

    override fun onEvent(event: Event) {
        when (event) {
            is Event.EmailChanged -> changeEmail(new = event.new)
            is Event.PasswordChanged -> changePassword(new = event.new)
            Event.AuthButtonClick -> trySignIn()
        }
    }


    private fun changeEmail(new: String) {
        _state.update { it.copy(email = new) }
    }

    private fun changePassword(new: String) {
        _state.update { it.copy(password = new) }
    }

    private fun trySignIn() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = repo.signIn(state.value.email, state.value.password)
            if (!response.success) {
                _state.update { it.copy(isLoading = false, error = response.message ?: "Произошла неизвестная ошибка") }
            }
        }
    }

}