package com.example.healthconnect.codelab.data.model.ditto

import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

/**
 * This class contains a Kotlin object representation of Ditto Things that contains the general
 * information of the athlete: the athlete's training goal, the suggested training plan and
 * some suggestions from the platform to the athlete
 */
class DittoGeneralInfoModel {

    @Serializable
    data class Thing(
        @EncodeDefault(EncodeDefault.Mode.NEVER) var thingId: String? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) var policyId: String? = null,
        var attributes: Attributes,
        var features: Features
    )

    @Serializable
    data class Attributes(
        var gender: Int,
        var height: Int,
        var weight: Double,
        var birthYear: Int,
        var runningYear: Int
    )

    @Serializable
    data class Features(
        var goal: Goal,
        val trainingPlan: TrainingPlan,
        var suggestions: Suggestions,
        var preferences: Preferences
    )

    @Serializable
    data class Goal(
        var properties: GoalProperties
    )

    @Serializable
    data class GoalProperties(
        var distance: Int,
        var seconds: Int,
        var estimations: List<Estimation>,
        var date: String
    )

    @Serializable
    data class Estimation(
        var date: String,
        var seconds: Int,
        var goalReachDate: String
    )

    @Serializable
    data class TrainingPlan(
        val properties: TrainingPlanProperties
    )

    @Serializable
    data class TrainingPlanProperties(
        val sessions: List<TrainingSession>
    )

    @Serializable
    data class TrainingSession(
        val day: String,
        val distance: Int,
        val times: Int,
        val rest: Int,
        val expectedTime: Int,
        val meanHeartRate: Int
    )

    @Serializable
    data class Suggestions(
        val properties: SuggestionsProperties
    )

    @Serializable
    data class SuggestionsProperties(
        val suggestions: List<Suggestion>
    )

    @Serializable
    data class Suggestion(
        val id: Int,
        val type: Int,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val distance: Int? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val seconds: Int? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val date: String? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val trainingDays: List<Int>? = null
    )

    @Serializable
    data class Preferences(
        var properties: PreferencesProperties
    )

    @Serializable
    data class PreferencesProperties(
        var trainingDays: List<Int>
    )
}

fun DittoGeneralInfo.Thing.toData(): DittoGeneralInfoModel.Thing =
    DittoGeneralInfoModel.Thing(attributes = attributes.toData(), features = features.toData())

fun DittoGeneralInfo.Attributes.toData(): DittoGeneralInfoModel.Attributes =
    DittoGeneralInfoModel.Attributes(gender, height, weight, birthYear, runningYear)

fun DittoGeneralInfo.Features.toData(): DittoGeneralInfoModel.Features =
    DittoGeneralInfoModel.Features(
        DittoGeneralInfoModel.Goal(goal.toData()),
        trainingPlan.toData(),
        suggestions.toData(),
        preferences.toData()
    )

fun DittoGeneralInfo.Goal.toData(): DittoGeneralInfoModel.GoalProperties =
    DittoGeneralInfoModel.GoalProperties(distance, seconds, estimations.map { it.toData() }, date.toString())

fun DittoGeneralInfo.Estimation.toData() : DittoGeneralInfoModel.Estimation =
    DittoGeneralInfoModel.Estimation(date.toString(), seconds, goalReachDate.toString())

fun DittoGeneralInfo.TrainingPlan.toData(): DittoGeneralInfoModel.TrainingPlan =
    DittoGeneralInfoModel.TrainingPlan(
        DittoGeneralInfoModel.TrainingPlanProperties(sessions.map { it.toData() })
    )

fun DittoGeneralInfo.TrainingSession.toData(): DittoGeneralInfoModel.TrainingSession =
    DittoGeneralInfoModel.TrainingSession(day.toString(), distance, times, rest, expectedTime, meanHeartRate)

fun DittoGeneralInfo.Suggestions.toData(): DittoGeneralInfoModel.Suggestions =
    DittoGeneralInfoModel.Suggestions(
        DittoGeneralInfoModel.SuggestionsProperties(suggestions.map { it.toData() })
    )

fun DittoGeneralInfo.Suggestion.toData(): DittoGeneralInfoModel.Suggestion {
    return when (this) {
        is DittoGeneralInfo.Suggestion.SmallerGoal -> {
            DittoGeneralInfoModel.Suggestion(
                id = id,
                type = type,
                distance = distance,
                seconds = seconds,
                date = date.toString()
            )
        }

        is DittoGeneralInfo.Suggestion.BiggerGoal -> {
            DittoGeneralInfoModel.Suggestion(
                id = id,
                type = type,
                distance = distance,
                seconds = seconds,
                date = date.toString()
            )
        }

        is DittoGeneralInfo.Suggestion.LessTrainingDays -> {
            DittoGeneralInfoModel.Suggestion(
                id = id,
                type = type,
                trainingDays = trainingDays
            )
        }

        is DittoGeneralInfo.Suggestion.MoreTrainingDays -> {
            DittoGeneralInfoModel.Suggestion(
                id = id,
                type = type,
                trainingDays = trainingDays
            )
        }
    }
}

fun DittoGeneralInfo.Preferences.toData(): DittoGeneralInfoModel.Preferences =
    DittoGeneralInfoModel.Preferences(
        DittoGeneralInfoModel.PreferencesProperties(trainingDays)
    )