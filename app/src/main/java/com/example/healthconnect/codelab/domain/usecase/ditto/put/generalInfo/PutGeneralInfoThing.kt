package com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutGeneralInfoThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<DittoGeneralInfo.Thing, DittoError, Unit>() {
    override suspend fun run(params: DittoGeneralInfo.Thing): Either<DittoError, Unit> {
        return repository.putGeneralInfoThing(params)
    }
}