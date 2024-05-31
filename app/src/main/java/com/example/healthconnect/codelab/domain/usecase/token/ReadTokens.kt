package com.example.healthconnect.codelab.domain.usecase.token

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.TokenRepository
import com.example.healthconnect.codelab.domain.model.token.Token
import com.example.healthconnect.codelab.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadTokens @Inject constructor(
    private val tokenRepository: TokenRepository
) : UseCase<Unit, Unit, Flow<Token>>() {
    override fun run(params: Unit): Either<Unit, Flow<Token>> {
        return tokenRepository.readToken()
    }
}