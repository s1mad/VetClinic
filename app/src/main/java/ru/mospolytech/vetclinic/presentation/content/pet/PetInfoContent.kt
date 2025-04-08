package ru.mospolytech.vetclinic.presentation.content.pet

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.presentation.content.pet.composable.BasePetInfoContent
import ru.mospolytech.vetclinic.presentation.content.pet.composable.ConfirmLogOutDialog
import ru.mospolytech.vetclinic.presentation.content.pet.composable.LogOutButtonContent
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
                    onTryAgainClick = {petInfoViewModel.onEvent(PetInfoViewModel.Event.TryAgainClick)}
                )
            }

            item {
                val color = if (state.selectedPet != null && !state.selectedPetLoading) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = targetAlpha)
                }
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = color,
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Текущие показатели")
                    }
                }
            }

            item {
                val color = if (state.selectedPet != null && !state.selectedPetLoading) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = targetAlpha)
                }
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = color,
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(min = 320.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Графики")
                    }
                }
            }


            item {
                LogOutButtonContent(
                    showConfirmDialog = { petInfoViewModel.onEvent(PetInfoViewModel.Event.LogOutButtonClick) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
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
    name = "Dark Theme"
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

    val pets = listOf(pet, pet, pet)

    VetClinicTheme {

        PetInfoContent(
            petInfoViewModel = object : PetInfoViewModel() {
                override val state: StateFlow<State> = MutableStateFlow(
                    State(
                        selectedPet = pet,
                        selectedPetLoading = false,
                        showSelectPetDialog = false,

                        showConfirmLogOutDialog = true,
                        pets = pets,
//                        petsLoading = false,
//                        error = NetworkError.SERVER_ERROR.userMessage()
                    )
                )

                override fun onEvent(event: Event) {}
            },
        )
    }
}



