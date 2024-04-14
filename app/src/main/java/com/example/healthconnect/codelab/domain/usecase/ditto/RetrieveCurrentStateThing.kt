package com.example.healthconnect.codelab.domain.usecase.ditto

import arrow.core.Either
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class RetrieveCurrentStateThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, Unit, DittoCurrentState.Thing>() {

    override suspend fun run(params: String): Either<Unit, DittoCurrentState.Thing> {
        return repository.retrieveCurrentStateThing(params)
    }
}