package com.example.healthconnect.codelab.domain.usecase.userInformation

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.UserInformationRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import java.time.LocalDate
import javax.inject.Inject

class WriteBirthdate @Inject constructor(
    private val repository: UserInformationRepository
) : SuspendUseCase<LocalDate, Unit, Unit>() {
    override suspend fun run(params: LocalDate): Either<Unit, Unit> {
        return repository.writeString("birthdate", params.toString())
    }
}