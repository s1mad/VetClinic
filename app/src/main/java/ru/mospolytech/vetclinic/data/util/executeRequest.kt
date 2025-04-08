package ru.mospolytech.vetclinic.data.util

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend inline fun <reified T> executeRequest(
    request: () -> Response<T>
): Result<T, NetworkError> {
    val response = try {
        request()
    } catch (e: IOException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        if (T::class == List::class) return Result.Success(emptyList<Any>() as T)
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: HttpException) {
        return Result.Error(NetworkError.UNKNOWN)
    } catch (e: Exception) {
        return Result.Error(NetworkError.UNKNOWN)
    }

    return when (response.code()) {
        in 200..299 -> {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else if (T::class == List::class) {
                Result.Success(emptyList<Any>() as T)
            } else {
                Result.Error(NetworkError.SERIALIZATION)
            }
        }
        401 -> Result.Error(NetworkError.UNAUTHORIZED)
        404 -> Result.Error(NetworkError.NOT_FOUND)
        408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
        413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
        else -> Result.Error(NetworkError.UNKNOWN)
    }
}