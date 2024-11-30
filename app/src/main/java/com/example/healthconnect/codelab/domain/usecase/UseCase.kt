package com.example.healthconnect.codelab.domain.usecase

import arrow.core.Either

abstract class UseCase<in P, out F, out T> {

    abstract fun run(params: P): Either<F, T>
    operator fun invoke(params: P, onResult: (Either<F, T>) -> Unit) {
        onResult(run(params))
    }
}