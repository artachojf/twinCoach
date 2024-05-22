package com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutGeneralInfoAttributes @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<PutGeneralInfoAttributesParams, DittoError, Unit>() {
    override suspend fun run(params: PutGeneralInfoAttributesParams): Either<DittoError, Unit> {
        return repository.putGeneralInfoAttributes(
            DittoGeneralInfo.thingId(params.googleId),
            params.attributes
        )
    }
}

data class PutGeneralInfoAttributesParams(
    val googleId: String,
    val attributes: DittoGeneralInfo.Attributes
)