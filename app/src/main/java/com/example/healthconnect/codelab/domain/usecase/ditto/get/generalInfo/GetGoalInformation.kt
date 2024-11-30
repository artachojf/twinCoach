package com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class GetGoalInformation @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, DittoError, DittoGeneralInfo.Goal?>() {
    override suspend fun run(params: String): Either<DittoError, DittoGeneralInfo.Goal?> {
        return repository.getGoalInformation(DittoGeneralInfo.thingId(params))
    }
}