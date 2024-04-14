package com.example.healthconnect.codelab.domain.usecase.userInformation

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.UserInformationRepository
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadUserInformation @Inject constructor(
    private val repository: UserInformationRepository
) : UseCase<Unit, Unit, Flow<UserInformation>>() {

    override fun run(params: Unit): Either<Unit, Flow<UserInformation>> =
        repository.readUserInformation()
}