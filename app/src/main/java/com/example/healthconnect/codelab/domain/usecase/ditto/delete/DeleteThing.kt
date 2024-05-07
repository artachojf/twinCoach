package com.example.healthconnect.codelab.domain.usecase.ditto.delete

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class DeleteThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, DittoError, Unit>() {
    override suspend fun run(params: String): Either<DittoError, Unit> {
        return repository.deleteThing(params)
    }
}