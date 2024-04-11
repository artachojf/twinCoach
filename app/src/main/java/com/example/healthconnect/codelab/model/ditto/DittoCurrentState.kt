package com.example.healthconnect.codelab.model.ditto

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * This class contains a Kotlin object representation of Ditto Things that contain the daily
 * workouts of the athlete and methods to create them easily
 */
class DittoCurrentState {
    data class TrainingSessionZone(var avgHr: Double, var time: Double, var distance: Double) {
        fun combine(s: TrainingSessionZone) {
            this.combine(s.avgHr, s.time, s.distance)
        }

        fun combine(hr: Double, time: Double, distance: Double) {
            this.avgHr = ((this.avgHr*this.time) + (hr*time)) / (this.time + time)
            this.time += time
            this.distance += distance
        }
    }

    data class TrainingSessionProperties(val zone1: TrainingSessionZone, val zone2: TrainingSessionZone,
                                         val zone3: TrainingSessionZone, val rest: TrainingSessionZone
    ) {
        fun combine(prop: TrainingSessionProperties) {
            this.zone1.combine(prop.zone1)
            this.zone2.combine(prop.zone2)
            this.zone3.combine(prop.zone3)
            this.rest.combine(prop.rest)
        }
    }

    data class TrainingSession(var properties: TrainingSessionProperties)

    data class SleepRatingProperties(var overall: Double){}

    data class SleepRating(val properties: SleepRatingProperties){}

    data class Features(val trainingSession: TrainingSession, val sleepRating: SleepRating){}

    data class Attributes(val googleId: String){}

    data class Thing(val attributes: Attributes, val features: Features){}

    fun createThing(z1: TrainingSessionZone, z2: TrainingSessionZone, z3: TrainingSessionZone,
                    rest: TrainingSessionZone, sleepRating: Double): Thing {
        val tsp = TrainingSessionProperties(z1, z2, z3, rest)
        val ts = TrainingSession(tsp)

        val srp = SleepRatingProperties(sleepRating)
        val sr = SleepRating(srp)

        return createThing(ts, sr)
    }

    fun createThing(z1: TrainingSessionZone, z2: TrainingSessionZone,
                    z3: TrainingSessionZone, rest: TrainingSessionZone
    ): Thing {
        return createThing(z1, z2, z3, rest, 0.0)
    }

    private fun createThing(trainingSession: TrainingSession, sleepRating: SleepRating): Thing {
        val features = Features(trainingSession, sleepRating)
        val attributes = Attributes(System.getProperty("GOOGLE_ID"))
        return Thing(attributes, features)
    }

    fun createRestThing(): Thing {
        return createThing(
            TrainingSessionZone(0.0, 0.0, 0.0), TrainingSessionZone(0.0, 0.0, 0.0),
            TrainingSessionZone(0.0, 0.0, 0.0), TrainingSessionZone(0.0, 0.0, 0.0)
        )
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