package ru.mospolytech.vetclinic.data.model.auth

enum class AuthorizationState {
    IDLE,
    LOADING,
    UNAUTHORIZED,
    AUTHORIZED,
}
