package ru.mospolytech.vetclinic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.mospolytech.vetclinic.data.api.AuthApi
import ru.mospolytech.vetclinic.data.api.PetApi
import ru.mospolytech.vetclinic.data.constant.VetClinicApiConstant
import ru.mospolytech.vetclinic.data.util.AuthManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(authManager: AuthManager): Interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = runBlocking { authManager.getToken() }

        val newRequest = originalRequest.newBuilder()
            .apply {
                if (token != null && originalRequest.url.encodedPath != VetClinicApiConstant.SIGN_IN) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            runBlocking { authManager.clearToken() }
        }

        response
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(VetClinicApiConstant.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun providePetApi(retrofit: Retrofit): PetApi = retrofit.create(PetApi::class.java)

}