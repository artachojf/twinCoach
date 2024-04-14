package com.example.healthconnect.codelab.domain.usecase.ditto

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutCurrentStateThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<DittoCurrentState.Thing, Unit, Unit>() {

    override suspend fun run(params: DittoCurrentState.Thing): Either<Unit, Unit> {
        return repository.putCurrentStateThing(params)
    }
}