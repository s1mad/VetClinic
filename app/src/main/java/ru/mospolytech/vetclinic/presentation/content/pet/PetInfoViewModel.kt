package ru.mospolytech.vetclinic.presentation.content.pet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.mospolytech.vetclinic.data.util.onError
import ru.mospolytech.vetclinic.data.util.onSuccess
import ru.mospolytech.vetclinic.data.util.userMessage
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.domain.repository.PetRepository
import ru.mospolytech.vetclinic.domain.usecase.LogOutUseCase
import javax.inject.Inject

abstract class PetInfoViewModel : ViewModel() {
    abstract val state: StateFlow<State>
    abstract fun onEvent(event: Event)

    @Serializable
    data class State(
        val selectedPet: Pet? = null,
        val selectedPetLoading: Boolean = false,
        val pets: List<Pet> = emptyList(),
        val petsLoading: Boolean = false,
        val error: String? = null,
        val showSelectPetDialog: Boolean = false,
        val showConfirmLogOutDialog: Boolean = false
    )

    @Serializable
    sealed class Event {
        data object SelectPetButtonClick : Event()
        data object SelectPetDialogDismiss : Event()
        data class SelectPet(val pet: Pet) : Event()
        data object TryAgainClick : Event()

        data object LogOutButtonClick : Event()
        data object LogOutDialogDismiss : Event()
        data object LogOutDialogConfirm : Event()
    }
}

@HiltViewModel
class PetInfoViewModelImpl @Inject constructor(
    private val repo: PetRepository,
    private val logOut: LogOutUseCase
) : PetInfoViewModel() {

    private val _state = MutableStateFlow(State())
    override val state get() = _state.asStateFlow()

    override fun onEvent(event: Event) {
        when (event) {
            Event.SelectPetButtonClick -> _state.update { it.copy(showSelectPetDialog = true) }
            Event.SelectPetDialogDismiss -> _state.update { it.copy(showSelectPetDialog = false) }
            is Event.SelectPet -> selectPet(pet = event.pet)
            Event.TryAgainClick -> loadPets()

            Event.LogOutButtonClick -> _state.update { it.copy(showConfirmLogOutDialog = true) }
            Event.LogOutDialogConfirm -> viewModelScope.launch { logOut() }
            Event.LogOutDialogDismiss -> _state.update { it.copy(showConfirmLogOutDialog = false) }
        }
    }

    private fun selectPet(pet: Pet) {
        _state.update { it.copy(selectedPet = pet, showSelectPetDialog = false) }
        viewModelScope.launch { repo.saveSelectedPet(pet) }
    }

    private fun loadPets() {
        _state.update { it.copy(selectedPetLoading = true, petsLoading = true) }

        val selectedPetJob = viewModelScope.launch {
            val pet = repo.getSelectedPet()
            if (pet != null) {
                _state.update { it.copy(selectedPet = pet, selectedPetLoading = false) }
            } else {
                _state.update { it.copy(selectedPet = null) }
            }
        }

        viewModelScope.launch {
            repo.getAllPets()
                .onSuccess { pets ->
                    if (pets.isEmpty()) {
                        _state.update {
                            it.copy(
                                pets = pets,
                                petsLoading = false,
                                selectedPetLoading = false,
                                error = "Не нашли ваших питомцев"
                            )
                        }
                        Log.d("asd", _state.value.toString())
                        return@onSuccess
                    }

                    _state.update { it.copy(pets = pets, petsLoading = false, error = null) }

                    selectedPetJob.join()
                    val selectedPet = _state.value.selectedPet

                    if (selectedPet == null) {

                        val pet = pets.firstOrNull()
                        _state.update { it.copy(selectedPet = pet, selectedPetLoading = false) }

                        if (pet != null) {
                            repo.saveSelectedPet(pet)
                        }

                    } else if (selectedPet !in pets) {

                        val updatedPet = pets.find { it.id == selectedPet.id }

                        if (updatedPet != null) {
                            _state.update { it.copy(selectedPet = updatedPet) }
                            repo.saveSelectedPet(updatedPet)
                        } else {
                            val pet = pets.firstOrNull() ?: return@onSuccess
                            _state.update { it.copy(selectedPet = pet) }
                            repo.saveSelectedPet(pet)
                        }

                    }
                }
                .onError { error ->
                    _state.update { it.copy(petsLoading = false, error = error.userMessage()) }

                    selectedPetJob.join()
                    _state.update { it.copy(selectedPetLoading = false) }
                }
        }
    }


    init {
        loadPets()
    }
}