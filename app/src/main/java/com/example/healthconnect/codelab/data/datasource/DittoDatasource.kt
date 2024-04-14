package com.example.healthconnect.codelab.data.datasource

import arrow.core.Either
import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import com.example.healthconnect.codelab.data.service.DittoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DittoDatasource @Inject constructor(
    private val dittoService: DittoService
) {

    suspend fun retrieveCurrentStateThing(
        thingId: String
    ): Either<Unit, DittoCurrentStateModel.Thing> {
        return withContext(Dispatchers.IO) {
            try {
                val thing = dittoService.retrieveCurrentStateThing(thingId)
                Either.Right(thing)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }

    suspend fun retrieveGeneralInfoThing(
        googleId: String
    ): Either<Unit, DittoGeneralInfoModel.Thing> {
        return withContext(Dispatchers.IO) {
            try {
                val thing = dittoService.retrieveGeneralInfoThing(googleId)
                Either.Right(thing)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }

    suspend fun putCurrentStateThing(
        googleId: String,
        thing: DittoCurrentStateModel.Thing
    ): Either<Unit, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dittoService.putCurrentStateThing(googleId, thing)
                Either.Right(Unit)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }

    suspend fun putGeneralInfoThing(
        thingId: String,
        thing: DittoGeneralInfoModel.Thing
    ): Either<Unit, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dittoService.putGeneralInfoThing(thingId, thing)
                Either.Right(Unit)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }

    suspend fun deleteThing(
        thingId: String
    ): Either<Unit, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dittoService.deleteThing(thingId)
                Either.Right(Unit)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }
}