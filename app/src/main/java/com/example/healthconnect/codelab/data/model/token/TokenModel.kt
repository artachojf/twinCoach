package com.example.healthconnect.codelab.data.model.token

import com.example.healthconnect.codelab.domain.model.token.Token

data class TokenModel(
    val exerciseSessionToken: String,
    val sleepToken: String,
    val stepsToken: String
)

fun Token.toData() = TokenModel(
    exerciseSessionToken,
    sleepToken,
    stepsToken
)