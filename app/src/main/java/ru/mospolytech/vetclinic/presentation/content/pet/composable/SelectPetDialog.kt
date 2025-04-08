package ru.mospolytech.vetclinic.presentation.content.pet.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.mospolytech.vetclinic.domain.model.Pet
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoViewModel
import ru.mospolytech.vetclinic.presentation.content.pet.util.toStringWithYears

@Composable
fun SelectPetDialog(
    state: PetInfoViewModel.State,
    targetAlpha: Float,
    onSelect: (Pet) -> Unit,
    onDismiss: () -> Unit,
) {
    if (state.showSelectPetDialog) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                shape = MaterialTheme.shapes.large
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Выберете питомца:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(vertical = 8.dp)
                        )
                    }
                    when {
                        state.petsLoading -> items(5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = targetAlpha))
                            )
                        }

                        state.error != null -> item {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                            )
                        }

                        state.pets.isEmpty() -> item {
                            Text(
                                text = "К вам не привязан ни один питомец",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                            )
                        }

                        else -> items(state.pets) { pet ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.1f))
                                    .clickable { onSelect(pet) }
                                    .padding(12.dp)
                            ) {
                                Text(text = "${pet.name} (${pet.animalType}) — ${pet.age.toStringWithYears()}")
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}