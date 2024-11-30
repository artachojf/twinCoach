package com.example.healthconnect.codelab.domain.usecase.userInformation

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.UserInformationRepository
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class WriteUserInformation @Inject constructor(
    private val repository: UserInformationRepository
) : SuspendUseCase<UserInformation, Unit, Unit>() {

    override suspend fun run(params: UserInformation): Either<Unit, Unit> =
        repository.writeUserInformation(params)
}