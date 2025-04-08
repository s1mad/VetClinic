package ru.mospolytech.vetclinic.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mospolytech.vetclinic.data.constant.VetClinicApiConstant
import ru.mospolytech.vetclinic.data.model.pet.PetDto

interface PetApi {

    @GET(VetClinicApiConstant.GET_ALL_PETS)
    suspend fun getAllPets(
        @Query("pet_id") petId: Int? = null,
        @Query("vet_id") vetId: Int? = null,
        @Query("owner_id") ownerId: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<PetDto>>

    @GET(VetClinicApiConstant.GET_ALL_PETS)
    suspend fun getPet(
        @Path("id") id: String
    ): Response<PetDto>
}
