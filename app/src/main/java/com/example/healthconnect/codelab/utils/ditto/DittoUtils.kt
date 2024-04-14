package com.example.healthconnect.codelab.utils.ditto

import com.example.healthconnect.codelab.data.model.ditto.DittoCurrentStateModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DittoUtils {

    companion object {
        val DITTO_THING_PREFIX = "org.ditto.twinCoach"
    }

    fun generateGeneralInfoThingId(googleId: String): String {
        return "$DITTO_THING_PREFIX:$googleId"
    }

    fun generateCurrentStateThingId(instant: Instant, googleId: String): String {
        return DITTO_THING_PREFIX + ":" + googleId + "-" +
                instant.atZone(ZoneOffset.systemDefault()).toLocalDate().toString()
    }

    fun generateCurrentStateThingId(localDate: LocalDate, googleId: String): String {
        return DITTO_THING_PREFIX + ":" + googleId + "-" +
                localDate.toString()
    }

    private fun createCurrentStateThing(
        googleId: String,
        trainingSession: DittoCurrentStateModel.TrainingSession,
        sleepRating: DittoCurrentStateModel.SleepRating
    ): DittoCurrentStateModel.Thing {
        val features = DittoCurrentStateModel.Features(trainingSession, sleepRating)
        val attributes = DittoCurrentStateModel.Attributes(googleId)
        return DittoCurrentStateModel.Thing(attributes, features)
    }

    fun createCurrentStateThing(
        googleId: String,
        z1: DittoCurrentStateModel.TrainingSessionZone,
        z2: DittoCurrentStateModel.TrainingSessionZone,
        z3: DittoCurrentStateModel.TrainingSessionZone,
        rest: DittoCurrentStateModel.TrainingSessionZone,
        sleepRating: Double
    ): DittoCurrentStateModel.Thing {

        val tsp = DittoCurrentStateModel.TrainingSessionProperties(z1, z2, z3, rest)
        val ts = DittoCurrentStateModel.TrainingSession(tsp)

        val srp = DittoCurrentStateModel.SleepRatingProperties(sleepRating)
        val sr = DittoCurrentStateModel.SleepRating(srp)

        return createCurrentStateThing(googleId, ts, sr)
    }

    fun createCurrentStateThing(
        googleId: String,
        z1: DittoCurrentStateModel.TrainingSessionZone,
        z2: DittoCurrentStateModel.TrainingSessionZone,
        z3: DittoCurrentStateModel.TrainingSessionZone,
        rest: DittoCurrentStateModel.TrainingSessionZone
    ): DittoCurrentStateModel.Thing = createCurrentStateThing(googleId, z1, z2, z3, rest, 0.0)

    fun createRestThing(googleId: String): DittoCurrentStateModel.Thing {
        return createCurrentStateThing(
            googleId,
            DittoCurrentStateModel.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentStateModel.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentStateModel.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentStateModel.TrainingSessionZone(0.0, 0.0, 0.0)
        )
    }
}