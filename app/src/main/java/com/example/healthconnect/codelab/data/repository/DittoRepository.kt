package com.example.healthconnect.codelab.data.repository

import arrow.core.Either
import com.example.healthconnect.codelab.data.datasource.DittoDatasource
import com.example.healthconnect.codelab.data.model.ditto.toData
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.model.ditto.toDomain
import javax.inject.Inject

class DittoRepository @Inject constructor(
    private val dittoDatasource: DittoDatasource
) {

    suspend fun retrieveCurrentStateThing(
        thingId: String
    ): Either<Unit, DittoCurrentState.Thing> =
        dittoDatasource.retrieveCurrentStateThing(thingId).map {
            it.toDomain(thingId)
        }

    suspend fun retrieveGeneralInfoThing(
        googleId: String
    ): Either<Unit, DittoGeneralInfo.Thing> =
        dittoDatasource.retrieveGeneralInfoThing(googleId).map {
            it.toDomain(googleId)
        }

    suspend fun putCurrentStateThing(
        thing: DittoCurrentState.Thing
    ): Either<Unit, Unit> =
        dittoDatasource.putCurrentStateThing(thing.thingId, thing.toData())

    suspend fun putGeneralInfoThing(
        thing: DittoGeneralInfo.Thing
    ): Either<Unit, Unit> =
        dittoDatasource.putGeneralInfoThing(thing.thingId, thing.toData())

    suspend fun deleteThing(
        thingId: String
    ): Either<Unit, Unit> =
        dittoDatasource.deleteThing(thingId)
}