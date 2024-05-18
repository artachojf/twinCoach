package com.example.healthconnect.codelab.data.datasource

import android.util.Log
import arrow.core.Either
import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.data.service.DittoService
import com.example.healthconnect.codelab.utils.parse.ResponseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DittoDatasource @Inject constructor(
    private val dittoService: DittoService
) {

    suspend fun retrieveCurrentStateThing(
        thingId: String
    ): Either<ResponseFailure, DittoCurrentStateModel.Thing?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.retrieveCurrentStateThing(thingId)
                if (response.code() == 404)
                    Either.Right(null)
                else
                    ResponseParser.parseResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun retrieveGeneralInfoThing(
        googleId: String
    ): Either<ResponseFailure, DittoGeneralInfoModel.Thing?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.retrieveGeneralInfoThing(googleId)
                if (response.code() == 404)
                    Either.Right(null)
                else
                    ResponseParser.parseResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun retrieveGeneralInfoFeatures(
        googleId: String
    ): Either<ResponseFailure, DittoGeneralInfoModel.Features?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.retrieveGeneralInfoFeatures(googleId)
                if (response.code() == 404)
                    Either.Right(null)
                else
                    ResponseParser.parseResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun retrieveSuggestedSessions(
        googleId: String
    ): Either<ResponseFailure, DittoGeneralInfoModel.TrainingPlanProperties?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.retrieveSuggestedSessions(googleId)
                if (response.code() == 404)
                    Either.Right(null)
                else
                    ResponseParser.parseResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun queryCurrentStateThings(
        googleId: String,
        size: Int = 100
    ): Either<ResponseFailure, DittoCurrentStateModel.QueryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val filter = "eq(attributes/googleId,\"$googleId\")"
                val option = "size($size)"
                val response = dittoService.queryCurrentStateThings(filter, option)
                ResponseParser.parseResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun putCurrentStateThing(
        googleId: String,
        thing: DittoCurrentStateModel.Thing
    ): Either<ResponseFailure, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.putCurrentStateThing(googleId, thing)
                ResponseParser.parseEmptyResponse(response)
            } catch (e: Exception) {
                Log.e("putError", e.stackTraceToString())
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun putGeneralInfoThing(
        thingId: String,
        thing: DittoGeneralInfoModel.Thing
    ): Either<ResponseFailure, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.putGeneralInfoThing(thingId, thing)
                ResponseParser.parseEmptyResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun deleteThing(
        thingId: String
    ): Either<ResponseFailure, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.deleteThing(thingId)
                ResponseParser.parseEmptyResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }

    suspend fun deletePolicy(
        policyId: String
    ): Either<ResponseFailure, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dittoService.deletePolicy(policyId)
                ResponseParser.parseEmptyResponse(response)
            } catch (e: Exception) {
                ResponseParser.parseError(e)
            }
        }
    }
}