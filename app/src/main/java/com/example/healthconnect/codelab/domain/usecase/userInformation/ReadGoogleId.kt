package com.example.healthconnect.codelab.domain.usecase.userInformation

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.UserInformationRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadGoogleId @Inject constructor(
    private val repository: UserInformationRepository
) : SuspendUseCase<Unit, Unit, Flow<String>>() {

    override suspend fun run(params: Unit): Either<Unit, Flow<String>> =
        repository.readString("googleId")
}