package ru.mospolytech.vetclinic.domain.repository

import ru.mospolytech.vetclinic.data.util.NetworkError
import ru.mospolytech.vetclinic.data.util.Result
import ru.mospolytech.vetclinic.domain.model.Metric

interface MetricsRepository {
    suspend fun getMetrics(
        interval: Interval,
        deviceId: Int,
        fromDate: String? = null,
        toDate: String? = null
    ): Result<List<Metric>, NetworkError>
}

enum class Interval {
    MINUTE,
    HOUR,
    DAY,
    WEEK
}