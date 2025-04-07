package ru.mospolytech.vetclinic.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ContentDestination {

    @Serializable
    data object Splash : ContentDestination()

    @Serializable
    data object Auth : ContentDestination()

    @Serializable
    data object PetInfo : ContentDestination()

}
