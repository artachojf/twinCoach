package com.example.healthconnect.codelab.domain.usecase.ditto.put.currentState

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutCurrentStateThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<DittoCurrentState.Thing, DittoError, Unit>() {

    override suspend fun run(params: DittoCurrentState.Thing): Either<DittoError, Unit> {
        return repository.putCurrentStateThing(params)
    }
}