package com.example.healthconnect.codelab.data.model.ditto

import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

/**
 * This class contains a Kotlin object representation of Ditto Things that contain the daily
 * workouts of the athlete and methods to create them easily
 */
class DittoCurrentStateModel {
    @Serializable
    data class QueryResponse(
        val items: List<Thing>
    )

    @Serializable
    data class Thing(
        @EncodeDefault(EncodeDefault.Mode.NEVER) val thingId: String? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val policyId: String? = null,
        val attributes: Attributes,
        val features: Features
    )

    @Serializable
    data class Attributes(
        val googleId: String,
        val date: String
    )

    @Serializable
    data class Features(
        val trainingSession: TrainingSession,
        val sleepRating: SleepRating
    )

    @Serializable
    data class TrainingSession(
        var properties: TrainingSessionProperties
    )

    @Serializable
    data class TrainingSessionProperties(
        val zone1: TrainingSessionZone,
        val zone2: TrainingSessionZone,
        val zone3: TrainingSessionZone,
        val rest: TrainingSessionZone,
        val laps: List<TrainingLap>
    )

    @Serializable
    data class TrainingSessionZone(
        var avgHr: Double,
        var time: Double,
        var distance: Double
    )

    @Serializable
    data class TrainingLap(
        var startTime: String,
        var distance: Double,
        var time: Double
    )

    @Serializable
    data class SleepRating(
        val properties: SleepRatingProperties
    )

    @Serializable
    data class SleepRatingProperties(
        var overall: Double
    )
}

fun DittoCurrentState.Thing.toData(): DittoCurrentStateModel.Thing =
    DittoCurrentStateModel.Thing(attributes = attributes.toData(), features = features.toData())

fun DittoCurrentState.Attributes.toData(): DittoCurrentStateModel.Attributes =
    DittoCurrentStateModel.Attributes(googleId, date.toString())

fun DittoCurrentState.Features.toData(): DittoCurrentStateModel.Features =
    DittoCurrentStateModel.Features(trainingSession.toData(), sleepRating.toData())

fun DittoCurrentState.TrainingSession.toData(): DittoCurrentStateModel.TrainingSession =
    DittoCurrentStateModel.TrainingSession(
        DittoCurrentStateModel.TrainingSessionProperties(
            zone1.toData(), zone2.toData(), zone3.toData(), rest.toData(), laps.map { it.toData() }
        )
    )

fun DittoCurrentState.TrainingSessionZone.toData(): DittoCurrentStateModel.TrainingSessionZone =
    DittoCurrentStateModel.TrainingSessionZone(avgHr, time, distance)

fun DittoCurrentState.TrainingLap.toData(): DittoCurrentStateModel.TrainingLap =
    DittoCurrentStateModel.TrainingLap(startTime.toString(), distance, time)

fun DittoCurrentState.SleepRating.toData(): DittoCurrentStateModel.SleepRating =
    DittoCurrentStateModel.SleepRating(
        DittoCurrentStateModel.SleepRatingProperties(overall)
    )