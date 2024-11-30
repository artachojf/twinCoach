package com.example.healthconnect.codelab.domain.usecase

import arrow.core.Either

abstract class SuspendUseCase<in P, out F, out T> {

    abstract suspend fun run(params: P): Either<F, T>
    suspend operator fun invoke(params: P, onResult: (Either<F, T>) -> Unit) {
        onResult(run(params))
    }
}