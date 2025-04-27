package ru.mospolytech.vetclinic.presentation.content.pet.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mospolytech.vetclinic.domain.model.Metric
import ru.mospolytech.vetclinic.domain.repository.Interval
import ru.mospolytech.vetclinic.presentation.content.pet.MetricType
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MetricsGraphContent(
    state: PetInfoViewModel.State,
    targetAlpha: Float,
    onMetricTypeSelected: (MetricType) -> Unit,
    onIntervalSelected: (Interval) -> Unit,
    modifier: Modifier = Modifier
) {
    val minHeight = 400.dp

    val color = if (state.selectedPet != null && !state.selectedPetLoading && !state.metricsLoading) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = targetAlpha)
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
            .height(minHeight)
    ) {
        AnimatedContent(
            targetState = animatedContentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(12.dp)
        ) { (isLoading, error, metrics) ->
            when {
                isLoading -> {}
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                metrics.isEmpty() -> EmptyData(modifier = Modifier.heightIn(min = minHeight - 32.dp))

                else -> {
                    Column {
                        Text(
                            text = "График",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .padding(top = 4.dp)
                        )
                        LineGraph(
                            metrics = metrics,
                            metricType = state.selectedMetricType,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(vertical = 12.dp, horizontal = 4.dp)
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MetricType.entries.forEachIndexed { index, metricType ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = MetricType.entries.size
                                    ),
                                    onClick = { onMetricTypeSelected(metricType) },
                                    selected = state.selectedMetricType == metricType,
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        activeContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    label = {
                                        Text(
                                            when (metricType) {
                                                MetricType.PULSE -> "Пульс"
                                                MetricType.TEMPERATURE -> "Температура"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Interval.entries.forEachIndexed { index, interval ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = Interval.entries.size
                                    ),
                                    onClick = { onIntervalSelected(interval) },
                                    selected = state.selectedInterval == interval,
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        activeContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    label = {
                                        Text(
                                            when (interval) {
                                                Interval.MINUTE -> "Минута"
                                                Interval.HOUR -> "Час"
                                                Interval.DAY -> "День"
                                                Interval.WEEK -> "Неделя"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineGraph(
    metrics: List<Metric>,
    metricType: MetricType,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(0.2f)

    val sortedMetrics = metrics.sortedBy { it.timestamp }
    val dataPoints = sortedMetrics.map { metric ->
        val timestamp = ZonedDateTime.parse(metric.timestamp, DateTimeFormatter.ISO_DATE_TIME).toEpochSecond()
        val value = when (metricType) {
            MetricType.PULSE -> metric.pulse.toFloat()
            MetricType.TEMPERATURE -> metric.temperature.toFloat()
        }
        timestamp to value
    }

    when {
        dataPoints.isEmpty() -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            val minTime = dataPoints.minOf { it.first }
            val maxTime = dataPoints.maxOf { it.first }
            val minValue = dataPoints.minOf { it.second }
            val maxValue = dataPoints.maxOf { it.second }
            val timeRange = if (maxTime == minTime) 1L else maxTime - minTime
            val valueRange = if (maxValue == minValue) 1f else maxValue - minValue

            Canvas(modifier = modifier) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val gridColor = gridColor
                val numHorizontalLines = 5
                for (i in 0..numHorizontalLines) {
                    val y = canvasHeight * (i.toFloat() / numHorizontalLines)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                }
                val path = Path()
                var isFirst = true
                dataPoints.forEach { (time, value) ->
                    val x = ((time - minTime).toFloat() / timeRange) * canvasWidth
                    val y = canvasHeight - ((value - minValue) / valueRange) * canvasHeight
                    if (isFirst) {
                        path.moveTo(x, y)
                        isFirst = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4f)
                )
                dataPoints.forEach { (time, value) ->
                    val x = ((time - minTime).toFloat() / timeRange) * canvasWidth
                    val y = canvasHeight - ((value - minValue) / valueRange) * canvasHeight
                    drawCircle(
                        color = lineColor,
                        radius = 6f,
                        center = Offset(x, y)
                    )
                }
            }
        }
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
            text = "График",
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