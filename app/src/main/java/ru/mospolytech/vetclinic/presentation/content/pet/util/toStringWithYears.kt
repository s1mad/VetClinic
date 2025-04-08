package ru.mospolytech.vetclinic.presentation.content.pet.util

fun Int.toStringWithYears(): String {
    val number = this
    val lastTwoDigits = number % 100
    val lastDigit = number % 10
    val form = when {
        lastTwoDigits in 11..14 -> "лет"
        lastDigit == 1 -> "год"
        lastDigit in 2..4 -> "года"
        else -> "лет"
    }
    return "$number $form"
}