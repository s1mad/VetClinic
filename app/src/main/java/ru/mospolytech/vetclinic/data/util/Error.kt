package ru.mospolytech.vetclinic.data.util

import kotlinx.serialization.Serializable

interface Error

@Serializable
enum class NetworkError : Error {
    REQUEST_TIMEOUT,      // Ошибка времени ожидания запроса
    UNAUTHORIZED,         // Ошибка авторизации
    CONFLICT,             // Конфликт запроса
    TOO_MANY_REQUESTS,    // Слишком много запросов
    NO_INTERNET,          // Отсутствие подключения к интернету
    NOT_FOUND,            // Ресурс не найден
    PAYLOAD_TOO_LARGE,    // Размер запроса слишком большой
    SERVER_ERROR,         // Ошибка на стороне сервера
    SERIALIZATION,        // Ошибка сериализации данных
    UNKNOWN;              // Неизвестная ошибка
}

fun Error.userMessage(): String {
    return when (this) {
        is NetworkError -> when (this) {
            NetworkError.REQUEST_TIMEOUT -> "Время ожидания запроса истекло. Попробуйте снова"
            NetworkError.UNAUTHORIZED -> "Ошибка авторизации. Проверьте свои учетные данные"
            NetworkError.CONFLICT -> "Конфликт запроса. Попробуйте изменить данные"
            NetworkError.TOO_MANY_REQUESTS -> "Слишком много запросов. Попробуйте позже"
            NetworkError.NO_INTERNET -> "Отсутствует подключение к интернету"
            NetworkError.NOT_FOUND -> "Не найдено"
            NetworkError.PAYLOAD_TOO_LARGE -> "Размер запроса слишком большой"
            NetworkError.SERVER_ERROR -> "Ошибка на сервере. Уже решаем. Попробуйте позже"
            NetworkError.SERIALIZATION -> "Ошибка сериализации данных. Попробуйте снова"
            NetworkError.UNKNOWN -> "Неизвестная ошибка. Проверьте ваше интернет соединение и попробуйте снова"
        }

        else -> "Неизвестная ошибка"
    }
}