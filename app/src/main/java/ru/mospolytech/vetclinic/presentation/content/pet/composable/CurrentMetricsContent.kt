package ru.mospolytech.vetclinic.presentation.content.pet.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoViewModel

@Composable
fun CurrentMetricsContent(
    state: PetInfoViewModel.State,
    targetAlpha: Float,
    modifier: Modifier = Modifier
) {
    val minHeight = 180.dp

    val color = if (state.metricsLoading || state.selectedPetLoading || state.selectedPet == null) {
        MaterialTheme.colorScheme.surface.copy(alpha = targetAlpha)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val animatedContentState = remember(state.metricsLoading, state.metricsError, state.metrics, state.selectedPet) {
        Triple(
            state.metricsLoading || state.selectedPetLoading || state.selectedPet == null,
            state.metricsError,
            state.metrics
        )
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
    ) {
        AnimatedContent(
            targetState = animatedContentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .heightIn(min = minHeight)
                .padding(16.dp)
        ) { (isLoading, error, metrics) ->
            when {
                isLoading -> {}
                error != null -> {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    val latestMetric = metrics.maxByOrNull { it.timestamp }
                    if (latestMetric != null && metrics.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Текущие показатели",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            MetricRow(
                                metric = "Пульс",
                                value = "${latestMetric.pulse} уд/мин",
                            )
                            MetricRow(
                                metric = "Температура",
                                value = "${latestMetric.temperature} °C",
                            )
                            MetricRow(
                                metric = "Мышечная активность",
                                value = "...",
                            )
                            MetricRow(
                                metric = "Тензодатчик",
                                value = "...",
                            )
                        }
                    } else EmptyData(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minHeight - 32.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    metric: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = metric,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyData(
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Текущие показатели",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = "Нет данных",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

    }
}