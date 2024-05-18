package com.example.healthconnect.codelab.domain.model.ditto

import android.util.Log
import com.example.healthconnect.codelab.BuildConfig
import com.example.healthconnect.codelab.data.model.ditto.DittoGeneralInfoModel
import java.io.Serializable
import java.time.LocalDate

class DittoGeneralInfo {

    data class Thing(
        var thingId: String,
        var policyId: String? = null,
        var attributes: Attributes,
        var features: Features
    )

    data class Attributes(
        var gender: Int,
        var height: Int,
        var weight: Double,
        var birthYear: Int,
        var runningYear: Int
    )

    data class Features(
        var goal: Goal,
        val trainingPlan: TrainingPlan,
        var suggestions: Suggestions,
        var preferences: Preferences
    )

    data class Goal(
        var distance: Int,
        var seconds: Int,
        var estimations: List<Estimation>,
        var date: LocalDate
    )

    data class Estimation(
        var date: LocalDate,
        var seconds: Int,
        var goalReachDate: LocalDate
    )

    data class TrainingPlan(
        val sessions: List<TrainingSession>
    )

    data class TrainingSession(
        val day: LocalDate,
        val distance: Int,
        val times: Int,
        val rest: Int,
        val expectedTime: Int,
        val meanHeartRate: Int
    ) : Serializable

    data class Suggestions(
        var suggestions: List<Suggestion>
    )

    sealed class Suggestion(open val id: Int, val type: Int) {
        data class SmallerGoal(
            override val id: Int,
            val distance: Int,
            val seconds: Int,
            val date: LocalDate
        ) : Suggestion(id,0)

        data class BiggerGoal(
            override val id: Int,
            val distance: Int,
            val seconds: Int,
            val date: LocalDate
        ) : Suggestion(id,1)

        data class LessTrainingDays(
            override val id: Int,
            val trainingDays: List<Int>
        ) : Suggestion(id,2)

        data class MoreTrainingDays(
            override val id: Int,
            val trainingDays: List<Int>
        ) : Suggestion(id,3)
    }

    data class Preferences(
        var trainingDays: List<Int>
    )

    companion object {
        fun thingId(googleId: String): String {
            return BuildConfig.DITTO_THING_PREFIX + ":" + googleId
        }
    }
}

fun DittoGeneralInfoModel.Thing.toDomain(): DittoGeneralInfo.Thing =
    DittoGeneralInfo.Thing(thingId!!, policyId, attributes.toDomain(), features.toDomain())

fun DittoGeneralInfoModel.Attributes.toDomain(): DittoGeneralInfo.Attributes =
    DittoGeneralInfo.Attributes(gender, height, weight, birthYear, runningYear)

fun DittoGeneralInfoModel.Features.toDomain(): DittoGeneralInfo.Features =
    DittoGeneralInfo.Features(
        goal.properties.toDomain(),
        trainingPlan.toDomain(),
        suggestions.toDomain(),
        preferences.toDomain()
    )

fun DittoGeneralInfoModel.GoalProperties.toDomain(): DittoGeneralInfo.Goal =
    DittoGeneralInfo.Goal(
        distance,
        seconds,
        estimations.map { it.toDomain() },
        LocalDate.parse(date)
    )

fun DittoGeneralInfoModel.Estimation.toDomain(): DittoGeneralInfo.Estimation =
    DittoGeneralInfo.Estimation(LocalDate.parse(date), seconds, LocalDate.parse(goalReachDate))

fun DittoGeneralInfoModel.TrainingPlan.toDomain(): DittoGeneralInfo.TrainingPlan =
    DittoGeneralInfo.TrainingPlan(properties.sessions.map { it.toDomain() })

fun DittoGeneralInfoModel.TrainingPlanProperties.toDomain(): DittoGeneralInfo.TrainingPlan =
    DittoGeneralInfo.TrainingPlan(sessions.map { it.toDomain() })

fun DittoGeneralInfoModel.TrainingSession.toDomain(): DittoGeneralInfo.TrainingSession =
    DittoGeneralInfo.TrainingSession(
        LocalDate.parse(day), distance, times, rest, expectedTime, meanHeartRate
    )

fun DittoGeneralInfoModel.Suggestions.toDomain(): DittoGeneralInfo.Suggestions =
    DittoGeneralInfo.Suggestions(properties.suggestions.map { it.toDomain() })

fun DittoGeneralInfoModel.Suggestion.toDomain(): DittoGeneralInfo.Suggestion {
    return when (type) {
        0 -> {
            DittoGeneralInfo.Suggestion.SmallerGoal(
                id, distance!!, seconds!!, LocalDate.parse(date!!)
            )
        }

        1 -> {
            DittoGeneralInfo.Suggestion.BiggerGoal(
                id, distance!!, seconds!!, LocalDate.parse(date!!)
            )
        }

        2 -> {
            DittoGeneralInfo.Suggestion.LessTrainingDays(id, trainingDays!!)
        }

        3 -> {
            DittoGeneralInfo.Suggestion.LessTrainingDays(id, trainingDays!!)
        }

        else -> {
            DittoGeneralInfo.Suggestion.SmallerGoal(
                id, distance!!, seconds!!, LocalDate.parse(date!!)
            )
        }
    }
}

fun DittoGeneralInfoModel.Preferences.toDomain(): DittoGeneralInfo.Preferences =
    DittoGeneralInfo.Preferences(properties.trainingDays)