package com.example.healthconnect.codelab.domain.model.ditto

import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel

/**
 * This class contains a Kotlin object representation of Ditto Things that contain the daily
 * workouts of the athlete and methods to create them easily
 */
class DittoCurrentState {

    data class Thing(
        var thingId: String,
        val attributes: Attributes,
        val features: Features
    )

    data class Attributes(
        val googleId: String
    )

    data class Features(
        val trainingSession: TrainingSession,
        val sleepRating: SleepRating
    )

    data class TrainingSession(
        val zone1: TrainingSessionZone,
        val zone2: TrainingSessionZone,
        val zone3: TrainingSessionZone,
        val rest: TrainingSessionZone
    ) {
        fun combine(prop: TrainingSession) {
            this.zone1.combine(prop.zone1)
            this.zone2.combine(prop.zone2)
            this.zone3.combine(prop.zone3)
            this.rest.combine(prop.rest)
        }
    }

    data class TrainingSessionZone(
        var avgHr: Double,
        var time: Double,
        var distance: Double
    ) {
        fun combine(s: TrainingSessionZone) {
            this.combine(s.avgHr, s.time, s.distance)
        }

        fun combine(hr: Double, time: Double, distance: Double) {
            this.avgHr = ((this.avgHr*this.time) + (hr*time)) / (this.time + time)
            this.time += time
            this.distance += distance
        }
    }

    data class SleepRating(
        var overall: Double
    )
}

fun DittoCurrentStateModel.Thing.toDomain(thingId: String): DittoCurrentState.Thing =
    DittoCurrentState.Thing(thingId, attributes.toDomain(), features.toDomain())

fun DittoCurrentStateModel.Attributes.toDomain(): DittoCurrentState.Attributes =
    DittoCurrentState.Attributes(googleId)

fun DittoCurrentStateModel.Features.toDomain(): DittoCurrentState.Features =
    DittoCurrentState.Features(trainingSession.toDomain(), sleepRating.toDomain())

fun DittoCurrentStateModel.TrainingSession.toDomain(): DittoCurrentState.TrainingSession =
    DittoCurrentState.TrainingSession(
        properties.zone1.toDomain(),
        properties.zone2.toDomain(),
        properties.zone3.toDomain(),
        properties.rest.toDomain()
    )

fun DittoCurrentStateModel.TrainingSessionZone.toDomain(): DittoCurrentState.TrainingSessionZone =
    DittoCurrentState.TrainingSessionZone(avgHr, time, distance)

fun DittoCurrentStateModel.SleepRating.toDomain(): DittoCurrentState.SleepRating =
    DittoCurrentState.SleepRating(properties.overall)