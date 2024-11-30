package com.example.healthconnect.codelab.data.repository

import arrow.core.Either
import com.example.healthconnect.codelab.data.datasource.DatastoreDatasource
import com.example.healthconnect.codelab.data.model.userInformation.toData
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.model.userInformation.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInformationRepository @Inject constructor(
    private val datasource: DatastoreDatasource
) {

    fun readString(key: String): Either<Unit, Flow<String>> =
        datasource.readString(key)

    suspend fun writeString(key: String, value: String): Either<Unit, Unit> =
        datasource.writeString(key, value)

    fun readUserInformation(): Either<Unit, Flow<UserInformation>> =
        datasource.readUserInformation().map { flow ->
            flow.map { it.toDomain() }
        }

    suspend fun writeUserInformation(user: UserInformation): Either<Unit, Unit> =
        datasource.writeUserInformation(user.toData())
}