package com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState

import java.time.LocalDate

data class GetCurrentStateInput(
    val googleId: String,
    val date: LocalDate
)