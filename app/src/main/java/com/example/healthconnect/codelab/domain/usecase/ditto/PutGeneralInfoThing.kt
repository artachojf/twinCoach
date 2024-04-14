package com.example.healthconnect.codelab.domain.usecase.ditto

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutGeneralInfoThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<DittoGeneralInfo.Thing, Unit, Unit>() {
    override suspend fun run(params: DittoGeneralInfo.Thing): Either<Unit, Unit> {
        return repository.putGeneralInfoThing(params)
    }
}