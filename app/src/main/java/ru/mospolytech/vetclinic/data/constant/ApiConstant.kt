package ru.mospolytech.vetclinic.data.constant

object VetClinicApiConstant {
//    const val BASE_URL = "http://217.114.6.189"
    const val BASE_URL = "http://10.0.2.2" // FIXME

    private const val AUTH_PORT = ":8083"
    const val SIGN_IN = "$BASE_URL$AUTH_PORT/auth/v1/sign-in"

    private const val PETS_PORT = ":8081"
    const val GET_ALL_PETS = "$BASE_URL$PETS_PORT/info/v1/pets"
    const val GET_PET = "$BASE_URL$PETS_PORT/info/v1/pets/{id}"

    private const val METRICS_PORT = ":8084"
    const val GET_METRICS = "$BASE_URL$METRICS_PORT/metrics"
}
