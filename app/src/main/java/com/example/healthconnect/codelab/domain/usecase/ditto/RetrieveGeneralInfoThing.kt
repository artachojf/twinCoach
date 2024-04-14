package com.example.healthconnect.codelab.domain.usecase.ditto

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class RetrieveGeneralInfoThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, Unit, DittoGeneralInfo.Thing>() {
    override suspend fun run(params: String): Either<Unit, DittoGeneralInfo.Thing> {
        return repository.retrieveGeneralInfoThing(params)
    }
}