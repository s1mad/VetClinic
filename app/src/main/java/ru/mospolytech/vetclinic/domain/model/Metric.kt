package ru.mospolytech.vetclinic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Metric(
    val deviceId: Int,
    val id: Int,
    val loadCell: LoadCell,
    val muscleActivity: MuscleActivity,
    val pulse: Int,
    val temperature: Int,
    val timestamp: String
)

@Serializable
data class LoadCell(
    val output1: Int,
    val output2: Int
)

@Serializable
data class MuscleActivity(
    val output1: Int,
    val output2: Int
)