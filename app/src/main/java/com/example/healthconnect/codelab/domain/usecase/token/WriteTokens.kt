package com.example.healthconnect.codelab.domain.usecase.token

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.TokenRepository
import com.example.healthconnect.codelab.domain.model.token.Token
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class WriteTokens @Inject constructor(
    private val tokenRepository: TokenRepository
) : SuspendUseCase<Token, Unit, Unit>() {
    override suspend fun run(params: Token): Either<Unit, Unit> {
        return tokenRepository.writeToken(params)
    }
}