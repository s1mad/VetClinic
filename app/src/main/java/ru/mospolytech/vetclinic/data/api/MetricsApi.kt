package ru.mospolytech.vetclinic.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.mospolytech.vetclinic.data.constant.VetClinicApiConstant
import ru.mospolytech.vetclinic.data.model.metrics.MetricsDto

interface MetricsApi {
    @GET(VetClinicApiConstant.GET_METRICS)
    suspend fun getAllPets(
        @Query("interval") interval: String,
        @Query("device_id") deviceId: Int,
        @Query("from_date") fromDate: String? = null,
        @Query("to_date") toDate: String? = null,
    ): Response<MetricsDto>
}