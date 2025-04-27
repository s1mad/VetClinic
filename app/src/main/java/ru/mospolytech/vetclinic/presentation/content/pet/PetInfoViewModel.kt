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
import ru.mospolytech.vetclinic.domain.model.Metric
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.domain.repository.Interval
import ru.mospolytech.vetclinic.domain.repository.MetricsRepository
import ru.mospolytech.vetclinic.domain.repository.PetRepository
import ru.mospolytech.vetclinic.domain.usecase.LogOutUseCase
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
        val showConfirmLogOutDialog: Boolean = false,
        val metrics: List<Metric> = emptyList(),
        val metricsLoading: Boolean = false,
        val metricsError: String? = null,
        val selectedMetricType: MetricType = MetricType.PULSE,
        val selectedInterval: Interval = Interval.DAY
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
        data class SelectMetricType(val metricType: MetricType) : Event()
        data class SelectInterval(val interval: Interval) : Event()
    }
}

enum class MetricType {
    PULSE,
    TEMPERATURE
}

@HiltViewModel
class PetInfoViewModelImpl @Inject constructor(
    private val petRepo: PetRepository,
    private val metricsRepo: MetricsRepository,
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
            is Event.SelectMetricType -> {
                _state.update { it.copy(selectedMetricType = event.metricType) }
                loadMetrics()
            }
            is Event.SelectInterval -> {
                _state.update { it.copy(selectedInterval = event.interval) }
                loadMetrics()
            }
        }
    }

    private fun selectPet(pet: Pet) {
        _state.update { it.copy(selectedPet = pet, showSelectPetDialog = false) }
        viewModelScope.launch {
            petRepo.saveSelectedPet(pet)
            loadMetrics()
        }
    }

    private fun loadPets() {
        _state.update { it.copy(selectedPetLoading = true, petsLoading = true) }

        val selectedPetJob = viewModelScope.launch {
            val pet = petRepo.getSelectedPet()
            if (pet != null) {
                _state.update { it.copy(selectedPet = pet, selectedPetLoading = false) }
            } else {
                _state.update { it.copy(selectedPet = null) }
            }
        }

        viewModelScope.launch {
            petRepo.getAllPets()
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
                        Log.d("PetInfoViewModel", _state.value.toString())
                        return@onSuccess
                    }

                    _state.update { it.copy(pets = pets, petsLoading = false, error = null) }

                    selectedPetJob.join()
                    val selectedPet = _state.value.selectedPet

                    if (selectedPet == null) {
                        val pet = pets.firstOrNull()
                        _state.update { it.copy(selectedPet = pet, selectedPetLoading = false) }
                        if (pet != null) {
                            petRepo.saveSelectedPet(pet)
                        }
                    } else if (selectedPet !in pets) {
                        val updatedPet = pets.find { it.id == selectedPet.id }
                        if (updatedPet != null) {
                            _state.update { it.copy(selectedPet = updatedPet) }
                            petRepo.saveSelectedPet(updatedPet)
                        } else {
                            val pet = pets.firstOrNull() ?: return@onSuccess
                            _state.update { it.copy(selectedPet = pet) }
                            petRepo.saveSelectedPet(pet)
                        }
                    }
                    loadMetrics()
                }
                .onError { error ->
                    _state.update { it.copy(petsLoading = false, error = error.userMessage()) }
                    selectedPetJob.join()
                    _state.update { it.copy(selectedPetLoading = false) }
                }
        }
    }

    private fun loadMetrics() {
        val pet = _state.value.selectedPet ?: return
        val interval = _state.value.selectedInterval

        _state.update { it.copy(metricsLoading = true, metricsError = null) }

        viewModelScope.launch {
            val toDate = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)
            val fromDate = when (interval) {
                Interval.MINUTE -> ZonedDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_INSTANT)
                Interval.HOUR -> ZonedDateTime.now().minusHours(24).format(DateTimeFormatter.ISO_INSTANT)
                Interval.DAY -> ZonedDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_INSTANT)
                Interval.WEEK -> ZonedDateTime.now().minusWeeks(4).format(DateTimeFormatter.ISO_INSTANT)
            }

            metricsRepo.getMetrics(
                interval = interval,
                deviceId = pet.id,
                fromDate = fromDate,
                toDate = toDate
            )
                .onSuccess { metrics ->
                    _state.update {
                        it.copy(
                            metrics = metrics,
                            metricsLoading = false,
                            metricsError = null
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            metrics = emptyList(),
                            metricsLoading = false,
                            metricsError = error.userMessage()
                        )
                    }
                }
        }
    }

    init {
        loadPets()
    }
}