package com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutGoal @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<PutGoalParams, DittoError, Unit>() {
    override suspend fun run(params: PutGoalParams): Either<DittoError, Unit> {
        return repository.putGeneralInfoGoal(DittoGeneralInfo.thingId(params.googleId), params.goal)
    }
}

data class PutGoalParams(
    val googleId: String,
    val goal: DittoGeneralInfo.Goal
)