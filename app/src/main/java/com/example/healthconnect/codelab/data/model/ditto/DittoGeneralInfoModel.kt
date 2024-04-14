package com.example.healthconnect.codelab.data.model.ditto

import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import java.time.Year

/**
 * This class contains a Kotlin object representation of Ditto Things that contains the general
 * information of the athlete: the athlete's training goal, the suggested training plan and
 * some suggestions from the platform to the athlete
 */
class DittoGeneralInfoModel {

    data class Thing(
        var attributes: Attributes,
        var features: Features
    )

    data class Attributes(
        var gender: Int,
        var height: Int,
        var weight: Double,
        var birthYear: Year,
        var runningYear: Year
    )

    data class Features(
        var goal: Goal,
        val trainingPlan: TrainingPlan,
        var suggestions: Suggestions,
        var preferences: Preferences
    )

    data class Goal(
        var properties: GoalProperties
    )

    data class GoalProperties(
        var distance: Int,
        var seconds: Int,
        var date: String
    )

    data class TrainingPlan(
        val properties: TrainingPlanProperties
    )

    data class TrainingPlanProperties(
        val sessions: List<TrainingSession>
    )

    data class TrainingSession(
        val day: String,
        val distance: Int,
        val times: Int,
        val rest: Int,
        val expectedTime: Int
    )

    data class Suggestions(
        val properties: SuggestionsProperties
    )

    data class SuggestionsProperties(
        val suggestions: List<GoalProperties>
    )

    data class Preferences(
        var properties: PreferencesProperties
    )

    data class PreferencesProperties(
        var trainingDays: List<Int>
    )
}

fun DittoGeneralInfo.Thing.toData(): DittoGeneralInfoModel.Thing =
    DittoGeneralInfoModel.Thing(attributes.toData(), features.toData())

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
    DittoGeneralInfoModel.GoalProperties(distance, seconds, date.toString())

fun DittoGeneralInfo.TrainingPlan.toData(): DittoGeneralInfoModel.TrainingPlan =
    DittoGeneralInfoModel.TrainingPlan(
        DittoGeneralInfoModel.TrainingPlanProperties(sessions.map { it.toData() })
    )

fun DittoGeneralInfo.TrainingSession.toData(): DittoGeneralInfoModel.TrainingSession =
    DittoGeneralInfoModel.TrainingSession(day.toString(), distance, times, rest, expectedTime)

fun DittoGeneralInfo.Suggestions.toData(): DittoGeneralInfoModel.Suggestions =
    DittoGeneralInfoModel.Suggestions(
        DittoGeneralInfoModel.SuggestionsProperties(suggestions.map { it.toData() })
    )

fun DittoGeneralInfo.Preferences.toData(): DittoGeneralInfoModel.Preferences =
    DittoGeneralInfoModel.Preferences(
        DittoGeneralInfoModel.PreferencesProperties(trainingDays)
    )