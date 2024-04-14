package com.example.healthconnect.codelab.domain.usecase.ditto

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class DeleteThing @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<String, Unit, Unit>() {
    override suspend fun run(params: String): Either<Unit, Unit> {
        return repository.deleteThing(params)
    }
}