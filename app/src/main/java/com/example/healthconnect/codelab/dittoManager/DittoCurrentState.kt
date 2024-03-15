package com.example.healthconnect.codelab.dittoManager

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * This class contains a Kotlin object representation of Ditto Things that contain the current
 * state of the athlete and methods to create them easily
 */
class DittoCurrentState {
    data class TrainingSessionProperties(var strength: Double, var aerobic_endurance: Double,
                                                          var anaerobic_endurance: Double, var fatigue: Double) {}

    data class TrainingSession(var properties: TrainingSessionProperties)

    data class SleepRatingProperties(var overall: Double){}

    data class SleepRating(val properties: SleepRatingProperties){}

    data class Features(val trainingSession: TrainingSession, val sleepRating: SleepRating){}

    data class Attributes(val googleId: String){}

    data class Thing(val attributes: Attributes, val features: Features){}

    fun createThing(strength: Double, aerobic_endurance: Double, anaerobic_endurance: Double,
                    fatigue: Double, sleepRating: Double): Thing {
        val tsp = TrainingSessionProperties(strength, aerobic_endurance, anaerobic_endurance, fatigue)
        val ts = TrainingSession(tsp)

        val srp = SleepRatingProperties(sleepRating)
        val sr = SleepRating(srp)

        return createThing(ts, sr)
    }

    fun createThing(strength: Double, aerobic_endurance: Double, anaerobic_endurance: Double, fatigue: Double): Thing {
        return createThing(strength, aerobic_endurance, anaerobic_endurance, fatigue, 0.0)
    }

    fun createRestThing(): Thing {
        return createThing(0.0, 0.0, 0.0, 0.0)
    }

    fun createRestThing(sleepRating: Double): Thing {
        return createThing(0.0, 0.0, 0.0, 0.0, sleepRating)
    }

    private fun createThing(trainingSession: TrainingSession, sleepRating: SleepRating): Thing {
        val features = Features(trainingSession, sleepRating)
        val attributes = Attributes(System.getProperty("GOOGLE_ID"))
        return Thing(attributes, features)
    }

    fun generateThingId(instant: Instant): String {
        return System.getProperty("DITTO_THING_PREFIX") + ":" + System.getProperty("GOOGLE_ID") + "-" +
                instant.atZone(ZoneOffset.systemDefault()).toLocalDate().toString()
    }

    fun generateThingId(localDate: LocalDate): String {
        return System.getProperty("DITTO_THING_PREFIX") + ":" + System.getProperty("GOOGLE_ID") + "-" +
                localDate.toString()
    }
}