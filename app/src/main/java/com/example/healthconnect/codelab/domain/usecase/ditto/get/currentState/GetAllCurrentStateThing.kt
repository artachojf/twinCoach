package com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class GetAllCurrentStateThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, DittoError, List<DittoCurrentState.Thing>>() {
    override suspend fun run(params: String): Either<DittoError, List<DittoCurrentState.Thing>> {
        return repository.getAllCurrentStateThings(params)
    }
}