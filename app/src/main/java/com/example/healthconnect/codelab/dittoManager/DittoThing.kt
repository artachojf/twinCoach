package com.example.healthconnect.codelab.dittoManager

data class DittoTrainingSessionProperties(var strength: Double, var aerobic_endurance: Double,
                                          var anaerobic_endurance: Double, var fatigue: Double) {}

data class DittoTrainingSession(var properties: DittoTrainingSessionProperties)

data class DittoSleepRatingProperties(var overall: Double){}

data class DittoSleepRating(val properties: DittoSleepRatingProperties){}

data class DittoFeatures(val trainingSession: DittoTrainingSession, val sleepRating: DittoSleepRating){}

data class DittoAttributes(val googleId: String){}

data class DittoThing(val attributes: DittoAttributes, val features: DittoFeatures){}

fun createDittoThing(strength: Double, aerobic_endurance: Double, anaerobic_endurance: Double,
                     fatigue: Double, sleepRating: Double): DittoThing {
    val tsp = DittoTrainingSessionProperties(strength, aerobic_endurance, anaerobic_endurance, fatigue)
    val ts = DittoTrainingSession(tsp)

    val srp = DittoSleepRatingProperties(sleepRating)
    val sr = DittoSleepRating(srp)

    return createDittoThing(ts, sr)
}

fun createDittoThing(strength: Double, aerobic_endurance: Double, anaerobic_endurance: Double, fatigue: Double): DittoThing {
    return createDittoThing(strength, aerobic_endurance, anaerobic_endurance, fatigue, 0.0)
}

fun createRestDittoThing(): DittoThing {
    return createDittoThing(0.0, 0.0, 0.0, 0.0)
}

fun createRestDittoThing(sleepRating: Double): DittoThing {
    return createDittoThing(0.0, 0.0, 0.0, 0.0, sleepRating)
}

private fun createDittoThing(trainingSession: DittoTrainingSession, sleepRating: DittoSleepRating): DittoThing {
    val features = DittoFeatures(trainingSession, sleepRating)
    val attributes = DittoAttributes(System.getProperty("GOOGLE_ID"))
    return DittoThing(attributes, features)
}