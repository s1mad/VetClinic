package ru.mospolytech.vetclinic.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.mospolytech.vetclinic.data.constant.VetClinicApiConstant
import ru.mospolytech.vetclinic.data.model.SignInRequest
import ru.mospolytech.vetclinic.data.model.SignInResponse

interface AuthApi {

    @POST(VetClinicApiConstant.SIGN_IN)
    fun signIn(@Body request: SignInRequest): Call<SignInResponse>

}
