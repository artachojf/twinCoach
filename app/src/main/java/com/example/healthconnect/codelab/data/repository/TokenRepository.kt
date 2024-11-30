package com.example.healthconnect.codelab.data.repository

import arrow.core.Either
import com.example.healthconnect.codelab.data.datasource.DatastoreDatasource
import com.example.healthconnect.codelab.data.model.token.TokenModel
import com.example.healthconnect.codelab.data.model.token.toData
import com.example.healthconnect.codelab.domain.model.token.Token
import com.example.healthconnect.codelab.domain.model.token.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val datasource: DatastoreDatasource
) {

    fun readToken(): Either<Unit, Flow<Token>> {
        return datasource.readToken().map { it.map { it.toDomain() } }
    }

    suspend fun writeToken(token: Token): Either<Unit, Unit> {
        return datasource.writeToken(token.toData())
    }
}