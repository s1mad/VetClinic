package ru.mospolytech.vetclinic.data.model.metrics

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mospolytech.vetclinic.domain.model.LoadCell
import ru.mospolytech.vetclinic.domain.model.Metric
import ru.mospolytech.vetclinic.domain.model.MuscleActivity

@Serializable
data class MetricsDto(
    @SerialName("result")
    val result: List<MetricsResultItemDto>
)

@Serializable
data class MetricsResultItemDto(
    @SerialName("device_id")
    val deviceId: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("load_cell")
    val loadCell: LoadCellDto,
    @SerialName("muscle_activity")
    val muscleActivity: MuscleActivityDto,
    @SerialName("pulse")
    val pulse: Int,
    @SerialName("temperature")
    val temperature: Int,
    @SerialName("timestamp")
    val timestamp: String
)

@Serializable
data class LoadCellDto(
    @SerialName("output1")
    val output1: Int,
    @SerialName("output2")
    val output2: Int
)

@Serializable
data class MuscleActivityDto(
    @SerialName("output1")
    val output1: Int,
    @SerialName("output2")
    val output2: Int
)

fun MetricsDto.toMetricList(): List<Metric> = result.map {
    it.toMetric()
}

fun MetricsResultItemDto.toMetric(): Metric = Metric(
    deviceId = deviceId,
    id = id,
    loadCell = loadCell.toLoadCell(),
    muscleActivity = muscleActivity.toMuscleActivity(),
    pulse = pulse,
    temperature = temperature,
    timestamp = timestamp
)

fun LoadCellDto.toLoadCell() = LoadCell(
    output1 = output1,
    output2 = output2
)

fun MuscleActivityDto.toMuscleActivity() = MuscleActivity(
    output1 = output1,
    output2 = output2
)