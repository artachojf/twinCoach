package com.example.healthconnect.codelab.dittoManager

import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.ZoneOffset

/**
 * This class contains a Kotlin object representation of Ditto Things that contains the general
 * information of the athlete: the athlete's training goal, the suggested training plan and
 * some suggestions from the platform to the athlete
 */
class DittoGeneralInfo {
    data class GoalProperties(var distance: Int, var seconds: Int, var date: String){}

    data class Goal(var properties: GoalProperties){}

    data class TrainingSession(val day: String, val distance: Int, val times: Int, val rest: Int, val expectedTime: Int){}

    data class TrainingPlanProperties(val sessions: List<TrainingSession>){}

    data class TrainingPlan(val properties: TrainingPlanProperties){}

    data class SuggestionsProperties(val suggestions: List<GoalProperties>){}

    data class Suggestions(val properties: SuggestionsProperties){}

    data class PreferencesProperties(var trainingDays: List<Int>)

    data class Preferences(var properties: PreferencesProperties) {}

    data class Features(var goal: Goal, val trainingPlan: TrainingPlan,
                        var suggestions: Suggestions, var preferences: Preferences){}

    data class Attributes(var gender: Int, var height: Int, var weight: Double,
                          var birthYear: Year, var runningYear: Year) {}

    data class Thing(var attributes: Attributes, var features: Features){}

    companion object {
        fun generateThingId(): String {
            return System.getProperty("DITTO_THING_PREFIX") + ":" + System.getProperty("GOOGLE_ID")
        }

        fun generateThingId(googleId: String): String {
            return System.getProperty("DITTO_THING_PREFIX") + ":" + googleId
        }
    }
}