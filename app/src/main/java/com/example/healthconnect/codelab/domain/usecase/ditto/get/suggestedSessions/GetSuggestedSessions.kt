package com.example.healthconnect.codelab.domain.usecase.ditto.get.suggestedSessions

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class GetSuggestedSessions @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, DittoError, DittoGeneralInfo.TrainingPlan?>() {
    override suspend fun run(params: String): Either<DittoError, DittoGeneralInfo.TrainingPlan?> {
        return repository.getSuggestedSessions(DittoGeneralInfo.thingId(params))
    }
}