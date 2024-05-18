package com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutGeneralInfoFeatures @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<PutGeneralInfoFeaturesParams, DittoError, Unit>() {
    override suspend fun run(params: PutGeneralInfoFeaturesParams): Either<DittoError, Unit> {
        return repository.putGeneralInfoFeatures(
            DittoGeneralInfo.thingId(params.googleId),
            params.features
        )
    }
}

data class PutGeneralInfoFeaturesParams(
    val googleId: String,
    val features: DittoGeneralInfo.Features
)