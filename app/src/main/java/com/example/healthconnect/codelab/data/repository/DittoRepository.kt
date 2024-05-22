package com.example.healthconnect.codelab.data.repository

import arrow.core.Either
import com.example.healthconnect.codelab.data.datasource.DittoDatasource
import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import com.example.healthconnect.codelab.data.model.ditto.toData
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.model.ditto.toDomain
import javax.inject.Inject

class DittoRepository @Inject constructor(
    private val dittoDatasource: DittoDatasource
) {

    suspend fun getCurrentStateThing(
        thingId: String
    ): Either<DittoError, DittoCurrentState.Thing?> {
        val response = dittoDatasource.retrieveCurrentStateThing(thingId)
        return response.fold(::handleError, ::handleCurrentState)
    }

    private fun handleCurrentState(
        thing: DittoCurrentStateModel.Thing?
    ): Either.Right<DittoCurrentState.Thing?> {
        return Either.Right(thing?.toDomain())
    }

    suspend fun getGeneralInfoThing(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.Thing?> {
        val response = dittoDatasource.retrieveGeneralInfoThing(googleId)
        return response.fold(::handleError, ::handleGeneralInfoThing)
    }

    private fun handleGeneralInfoThing(
        thing: DittoGeneralInfoModel.Thing?
    ): Either.Right<DittoGeneralInfo.Thing?> {
        return Either.Right(thing?.toDomain())
    }

    suspend fun getGeneralInfoFeatures(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.Features?> {
        val response = dittoDatasource.retrieveGeneralInfoFeatures(googleId)
        return response.fold(::handleError, ::handleGeneralInfoFeatures)
    }

    private fun handleGeneralInfoFeatures(
        thing: DittoGeneralInfoModel.Features?
    ): Either.Right<DittoGeneralInfo.Features?> {
        return Either.Right(thing?.toDomain())
    }

    suspend fun getSuggestedSessions(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.TrainingPlan?> {
        val response = dittoDatasource.retrieveSuggestedSessions(googleId)
        return response.fold(::handleError, ::handleSuggestedSessions)
    }

    private fun handleSuggestedSessions(
        plan: DittoGeneralInfoModel.TrainingPlanProperties?
    ): Either.Right<DittoGeneralInfo.TrainingPlan?> {
        return Either.Right(plan?.toDomain())
    }

    suspend fun getGoalInformation(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.Goal?> {
        val response = dittoDatasource.getGoalInformation(googleId)
        return response.fold(::handleError, ::handleGoalInformation)
    }

    private fun handleGoalInformation(
        goal: DittoGeneralInfoModel.GoalProperties?
    ): Either.Right<DittoGeneralInfo.Goal?> {
        return Either.Right(goal?.toDomain())
    }

    suspend fun getAttributes(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.Attributes?> {
        val response = dittoDatasource.getAttributes(googleId)
        return response.fold(::handleError, ::handleAttributes)
    }

    private fun handleAttributes(
        attributes: DittoGeneralInfoModel.Attributes?
    ): Either.Right<DittoGeneralInfo.Attributes?> {
        return Either.Right(attributes?.toDomain())
    }

    suspend fun getPreferences(
        googleId: String
    ): Either<DittoError, DittoGeneralInfo.Preferences?> {
        val response = dittoDatasource.getGeneralInfoPreferences(googleId)
        return response.fold(::handleError, ::handlePreferences)
    }

    private fun handlePreferences(
        preferences: DittoGeneralInfoModel.PreferencesProperties?
    ): Either.Right<DittoGeneralInfo.Preferences?> {
        return Either.Right(preferences?.let { DittoGeneralInfoModel.Preferences(it).toDomain() })
    }

    suspend fun getAllCurrentStateThings(
        googleId: String
    ): Either<DittoError, List<DittoCurrentState.Thing>> {
        val response = dittoDatasource.queryCurrentStateThings(googleId)
        return response.fold(::handleError, ::handleGetAll)
    }

    private fun handleGetAll(
        queryResponse: DittoCurrentStateModel.QueryResponse
    ): Either.Right<List<DittoCurrentState.Thing>> {
        return Either.Right(queryResponse.toDomain())
    }

    suspend fun putCurrentStateThing(
        thing: DittoCurrentState.Thing
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putCurrentStateThing(thing.thingId, thing.toData())
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun putGeneralInfoThing(
        thing: DittoGeneralInfo.Thing
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putGeneralInfoThing(thing.thingId, thing.toData())
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun putGeneralInfoFeatures(
        thingId: String,
        features: DittoGeneralInfo.Features
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putGeneralInfoFeatures(thingId, features.toData())
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun putGeneralInfoGoal(
        thingId: String,
        goal: DittoGeneralInfo.Goal
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putGeneralInfoGoal(thingId, goal.toData())
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun putGeneralInfoAttributes(
        thingId: String,
        attributes: DittoGeneralInfo.Attributes
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putGeneralInfoAttributes(thingId, attributes.toData())
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun putGeneralInfoPreferences(
        thingId: String,
        preferences: DittoGeneralInfo.Preferences
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.putGeneralInfoPreferences(thingId, preferences.toData().properties)
        return response.fold(::handleError, ::handleEmptySuccess)
    }

    suspend fun deleteThing(
        thingId: String
    ): Either<DittoError, Unit> {
        val response = dittoDatasource.deleteThing(thingId)
        val deleteResponse = response.fold(::handleError, ::handleEmptySuccess)
        return if (deleteResponse.isLeft()) deleteResponse
        else dittoDatasource.deletePolicy(thingId).fold(::handleError, ::handleEmptySuccess)

    }

    private fun handleEmptySuccess(unit: Unit): Either.Right<Unit> =
        Either.Right(unit)

    private fun handleError(error: ResponseFailure): Either.Left<DittoError> =
        Either.Left(DittoError(error))
}