package ru.mospolytech.vetclinic.presentation.content.pet

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mospolytech.vetclinic.domain.model.LoadCell
import ru.mospolytech.vetclinic.domain.model.Metric
import ru.mospolytech.vetclinic.domain.model.MuscleActivity
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.domain.repository.Interval
import ru.mospolytech.vetclinic.presentation.content.pet.composable.BasePetInfoContent
import ru.mospolytech.vetclinic.presentation.content.pet.composable.ConfirmLogOutDialog
import ru.mospolytech.vetclinic.presentation.content.pet.composable.CurrentMetricsContent
import ru.mospolytech.vetclinic.presentation.content.pet.composable.LogOutButtonContent
import ru.mospolytech.vetclinic.presentation.content.pet.composable.MetricsGraphContent
import ru.mospolytech.vetclinic.presentation.content.pet.composable.SelectPetDialog
import ru.mospolytech.vetclinic.presentation.theme.VetClinicTheme

@Composable
fun PetInfoContent(
    petInfoViewModel: PetInfoViewModel,
    modifier: Modifier = Modifier
) {
    val state by petInfoViewModel.state.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "AlphaTransition")
    val targetAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulsingAlpha"
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            item {
                BasePetInfoContent(
                    state = state,
                    targetAlpha = targetAlpha,
                    onSelectPetButtonClick = { petInfoViewModel.onEvent(PetInfoViewModel.Event.SelectPetButtonClick) },
                    onTryAgainClick = { petInfoViewModel.onEvent(PetInfoViewModel.Event.TryAgainClick) },
                )
            }

            item {
                CurrentMetricsContent(
                    state = state,
                    targetAlpha = targetAlpha,
                )
            }

            item {
                MetricsGraphContent(
                    state = state,
                    targetAlpha = targetAlpha,
                    onMetricTypeSelected = { metricType ->
                        petInfoViewModel.onEvent(PetInfoViewModel.Event.SelectMetricType(metricType))
                    },
                    onIntervalSelected = { interval ->
                        petInfoViewModel.onEvent(PetInfoViewModel.Event.SelectInterval(interval))
                    },
                )
            }

            item {
                LogOutButtonContent(
                    showConfirmDialog = { petInfoViewModel.onEvent(PetInfoViewModel.Event.LogOutButtonClick) },
                    modifier = Modifier
                )
            }
        }
    }

    SelectPetDialog(
        state = state,
        targetAlpha = targetAlpha,
        onSelect = { petInfoViewModel.onEvent(PetInfoViewModel.Event.SelectPet(it)) },
        onDismiss = { petInfoViewModel.onEvent(PetInfoViewModel.Event.SelectPetDialogDismiss) }
    )

    ConfirmLogOutDialog(
        visible = state.showConfirmLogOutDialog,
        onDismiss = { petInfoViewModel.onEvent(PetInfoViewModel.Event.LogOutDialogDismiss) },
        onConfirm = { petInfoViewModel.onEvent(PetInfoViewModel.Event.LogOutDialogConfirm) }
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Theme"
)
@Composable
private fun PetInfoContentPreview() {
    val pet = Pet(
        id = 1,
        name = "Клара",
        age = 6,
        animalType = "Кошка",
        behavior = "asd",
        condition = "asd",
        gender = "asd",
        researchStatus = "asd",
        weight = 123
    )

    val metrics = listOf(
            Metric(
                deviceId = 1,
                id = 1,
                loadCell = LoadCell(output1 = 10, output2 = 20),
                muscleActivity = MuscleActivity(output1 = 30, output2 = 40),
                pulse = 80,
                temperature = 37,
                timestamp = "2025-01-01T00:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 2,
                loadCell = LoadCell(output1 = 11, output2 = 21),
                muscleActivity = MuscleActivity(output1 = 31, output2 = 41),
                pulse = 82,
                temperature = 37,
                timestamp = "2025-01-01T01:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 3,
                loadCell = LoadCell(output1 = 12, output2 = 22),
                muscleActivity = MuscleActivity(output1 = 32, output2 = 42),
                pulse = 85,
                temperature = 38,
                timestamp = "2025-01-01T02:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 4,
                loadCell = LoadCell(output1 = 13, output2 = 23),
                muscleActivity = MuscleActivity(output1 = 33, output2 = 43),
                pulse = 78,
                temperature = 36,
                timestamp = "2025-01-01T03:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 5,
                loadCell = LoadCell(output1 = 14, output2 = 24),
                muscleActivity = MuscleActivity(output1 = 34, output2 = 44),
                pulse = 90,
                temperature = 38,
                timestamp = "2025-01-01T04:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 6,
                loadCell = LoadCell(output1 = 15, output2 = 25),
                muscleActivity = MuscleActivity(output1 = 35, output2 = 45),
                pulse = 88,
                temperature = 37,
                timestamp = "2025-01-01T05:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 7,
                loadCell = LoadCell(output1 = 16, output2 = 26),
                muscleActivity = MuscleActivity(output1 = 36, output2 = 46),
                pulse = 84,
                temperature = 37,
                timestamp = "2025-01-01T06:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 8,
                loadCell = LoadCell(output1 = 17, output2 = 27),
                muscleActivity = MuscleActivity(output1 = 37, output2 = 47),
                pulse = 86,
                temperature = 38,
                timestamp = "2025-01-01T07:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 9,
                loadCell = LoadCell(output1 = 18, output2 = 28),
                muscleActivity = MuscleActivity(output1 = 38, output2 = 48),
                pulse = 83,
                temperature = 37,
                timestamp = "2025-01-01T08:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 10,
                loadCell = LoadCell(output1 = 19, output2 = 29),
                muscleActivity = MuscleActivity(output1 = 39, output2 = 49),
                pulse = 81,
                temperature = 36,
                timestamp = "2025-01-01T09:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 11,
                loadCell = LoadCell(output1 = 20, output2 = 30),
                muscleActivity = MuscleActivity(output1 = 40, output2 = 50),
                pulse = 79,
                temperature = 37,
                timestamp = "2025-01-01T10:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 12,
                loadCell = LoadCell(output1 = 21, output2 = 31),
                muscleActivity = MuscleActivity(output1 = 41, output2 = 51),
                pulse = 87,
                temperature = 38,
                timestamp = "2025-01-01T11:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 13,
                loadCell = LoadCell(output1 = 22, output2 = 32),
                muscleActivity = MuscleActivity(output1 = 42, output2 = 52),
                pulse = 89,
                temperature = 37,
                timestamp = "2025-01-01T12:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 14,
                loadCell = LoadCell(output1 = 23, output2 = 33),
                muscleActivity = MuscleActivity(output1 = 43, output2 = 53),
                pulse = 82,
                temperature = 36,
                timestamp = "2025-01-01T13:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 15,
                loadCell = LoadCell(output1 = 24, output2 = 34),
                muscleActivity = MuscleActivity(output1 = 44, output2 = 54),
                pulse = 84,
                temperature = 37,
                timestamp = "2025-01-01T14:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 16,
                loadCell = LoadCell(output1 = 25, output2 = 35),
                muscleActivity = MuscleActivity(output1 = 45, output2 = 55),
                pulse = 86,
                temperature = 38,
                timestamp = "2025-01-01T15:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 17,
                loadCell = LoadCell(output1 = 26, output2 = 36),
                muscleActivity = MuscleActivity(output1 = 46, output2 = 56),
                pulse = 88,
                temperature = 37,
                timestamp = "2025-01-01T16:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 18,
                loadCell = LoadCell(output1 = 27, output2 = 37),
                muscleActivity = MuscleActivity(output1 = 47, output2 = 57),
                pulse = 85,
                temperature = 36,
                timestamp = "2025-01-01T17:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 19,
                loadCell = LoadCell(output1 = 28, output2 = 38),
                muscleActivity = MuscleActivity(output1 = 48, output2 = 58),
                pulse = 83,
                temperature = 37,
                timestamp = "2025-01-01T18:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 20,
                loadCell = LoadCell(output1 = 29, output2 = 39),
                muscleActivity = MuscleActivity(output1 = 49, output2 = 59),
                pulse = 81,
                temperature = 38,
                timestamp = "2025-01-01T19:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 21,
                loadCell = LoadCell(output1 = 30, output2 = 40),
                muscleActivity = MuscleActivity(output1 = 50, output2 = 60),
                pulse = 79,
                temperature = 37,
                timestamp = "2025-01-01T20:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 22,
                loadCell = LoadCell(output1 = 31, output2 = 41),
                muscleActivity = MuscleActivity(output1 = 51, output2 = 61),
                pulse = 87,
                temperature = 36,
                timestamp = "2025-01-01T21:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 23,
                loadCell = LoadCell(output1 = 32, output2 = 42),
                muscleActivity = MuscleActivity(output1 = 52, output2 = 62),
                pulse = 90,
                temperature = 38,
                timestamp = "2025-01-01T22:00:00Z"
            ),
            Metric(
                deviceId = 1,
                id = 24,
                loadCell = LoadCell(output1 = 33, output2 = 43),
                muscleActivity = MuscleActivity(output1 = 53, output2 = 63),
                pulse = 84,
                temperature = 37,
                timestamp = "2025-01-01T23:00:00Z"
            )
        )

    VetClinicTheme {
        PetInfoContent(
            petInfoViewModel = object : PetInfoViewModel() {
                override val state: StateFlow<State> = MutableStateFlow(
                    State(
                        selectedPet = pet,
                        selectedPetLoading = false,
                        showSelectPetDialog = false,
                        showConfirmLogOutDialog = false,
                        pets = listOf(pet),
                        metrics = metrics,
                        metricsLoading = false,
                        metricsError = null,
                        selectedMetricType = MetricType.PULSE,
                        selectedInterval = Interval.DAY
                    )
                )

                override fun onEvent(event: Event) {}
            }
        )
    }
}