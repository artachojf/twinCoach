package com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class GetCurrentStateThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<GetCurrentStateInput, DittoError, DittoCurrentState.Thing?>() {

    override suspend fun run(params: GetCurrentStateInput): Either<DittoError, DittoCurrentState.Thing?> {
        return repository.getCurrentStateThing(DittoCurrentState.thingId(params.googleId, params.date))
    }
}