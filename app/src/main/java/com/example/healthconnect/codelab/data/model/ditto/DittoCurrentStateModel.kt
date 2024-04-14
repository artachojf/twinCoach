package com.example.healthconnect.codelab.data.model.ditto

import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState

/**
 * This class contains a Kotlin object representation of Ditto Things that contain the daily
 * workouts of the athlete and methods to create them easily
 */
class DittoCurrentStateModel {
    data class Thing(
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
        var properties: TrainingSessionProperties
    )

    data class TrainingSessionProperties(
        val zone1: TrainingSessionZone,
        val zone2: TrainingSessionZone,
        val zone3: TrainingSessionZone,
        val rest: TrainingSessionZone
    )

    data class TrainingSessionZone(
        var avgHr: Double,
        var time: Double,
        var distance: Double
    )

    data class SleepRating(
        val properties: SleepRatingProperties
    )

    data class SleepRatingProperties(
        var overall: Double
    )
}

fun DittoCurrentState.Thing.toData(): DittoCurrentStateModel.Thing =
    DittoCurrentStateModel.Thing(attributes.toData(), features.toData())

fun DittoCurrentState.Attributes.toData(): DittoCurrentStateModel.Attributes =
    DittoCurrentStateModel.Attributes(googleId)

fun DittoCurrentState.Features.toData(): DittoCurrentStateModel.Features =
    DittoCurrentStateModel.Features(trainingSession.toData(), sleepRating.toData())

fun DittoCurrentState.TrainingSession.toData(): DittoCurrentStateModel.TrainingSession =
    DittoCurrentStateModel.TrainingSession(
        DittoCurrentStateModel.TrainingSessionProperties(
            zone1.toData(), zone2.toData(), zone3.toData(), rest.toData()
        )
    )

fun DittoCurrentState.TrainingSessionZone.toData(): DittoCurrentStateModel.TrainingSessionZone =
    DittoCurrentStateModel.TrainingSessionZone(avgHr, time, distance)

fun DittoCurrentState.SleepRating.toData(): DittoCurrentStateModel.SleepRating =
    DittoCurrentStateModel.SleepRating(
        DittoCurrentStateModel.SleepRatingProperties(overall)
    )