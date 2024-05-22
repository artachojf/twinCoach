package com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo

import arrow.core.Either
import com.example.healthconnect.codelab.data.repository.DittoRepository
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.SuspendUseCase
import javax.inject.Inject

class PutPreferences @Inject constructor(
    private val repository: DittoRepository
) : SuspendUseCase<PutPreferencesParams, DittoError, Unit>() {
    override suspend fun run(params: PutPreferencesParams): Either<DittoError, Unit> {
        return repository.putGeneralInfoPreferences(
            DittoGeneralInfo.thingId(params.googleId),
            params.preferences
        )
    }
}

data class PutPreferencesParams(
    val googleId: String,
    val preferences: DittoGeneralInfo.Preferences
)