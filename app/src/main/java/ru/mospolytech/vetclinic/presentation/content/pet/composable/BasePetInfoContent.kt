package ru.mospolytech.vetclinic.presentation.content.pet.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoViewModel
import ru.mospolytech.vetclinic.presentation.content.pet.util.toStringWithYears

@Preview
@Composable
fun BasePetInfoContentPreview() {
    BasePetInfoContent(
        state = PetInfoViewModel.State(
            selectedPet = Pet(
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
        ),
        targetAlpha = 1f,
        onSelectPetButtonClick = {},
        onTryAgainClick = {}
    )
}


@Composable
fun BasePetInfoContent(
    state: PetInfoViewModel.State,
    onSelectPetButtonClick: () -> Unit,
    onTryAgainClick: () -> Unit,
    targetAlpha: Float,
    modifier: Modifier = Modifier
) {

    val color = if (state.selectedPetLoading) {
        MaterialTheme.colorScheme.surface.copy(alpha = targetAlpha)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val animatedContentState = remember(state.selectedPetLoading, state.error, state.selectedPet) {
        Triple(state.selectedPetLoading, state.error, state.selectedPet)
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
    ) {
        AnimatedContent(
            targetState = animatedContentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
            contentAlignment = Alignment.Center,
        ) { (selectedPetLoading, error, selectedPet) ->
            when {
                selectedPetLoading -> {}

                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = error,
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onTryAgainClick,
                            modifier = Modifier
                                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                                .fillMaxWidth()
                                .heightIn(min = 44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = "Повторить попытку", fontSize = 16.sp)
                        }
                    }
                }

                selectedPet != null -> {
                    val firstText = if (state.selectedPet != null) with(state.selectedPet) {
                        "$name ($animalType) — ${age.toStringWithYears()}"
                    } else ""

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = firstText,
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onSelectPetButtonClick,
                            modifier = Modifier
                                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                                .fillMaxWidth()
                                .heightIn(min = 44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = "Сменить питомца", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}