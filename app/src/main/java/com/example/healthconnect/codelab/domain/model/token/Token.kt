package com.example.healthconnect.codelab.domain.model.token

import com.example.healthconnect.codelab.data.model.token.TokenModel

data class Token(
    val exerciseSessionToken: String,
    val sleepToken: String,
    val stepsToken: String
) {
    fun toList() = listOf(exerciseSessionToken, sleepToken, stepsToken)
}

fun TokenModel.toDomain() = Token(
    exerciseSessionToken,
    sleepToken,
    stepsToken
)