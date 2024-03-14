package com.example.healthconnect.codelab

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import com.couchbase.lite.MutableDocument
import com.example.healthconnect.codelab.couchbase.CouchbaseController
import com.example.healthconnect.codelab.dittoManager.DittoConnectionManager
import com.example.healthconnect.codelab.dittoManager.DittoThing
import com.example.healthconnect.codelab.dittoManager.createRestDittoThing
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Base64


class PeriodicDittoService : JobService() {

    private lateinit var hcManager: HealthConnectManager
    private val dcm = DittoConnectionManager()

    override fun onStartJob(params: JobParameters?): Boolean {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    val couchbaseController = CouchbaseController(applicationContext, "twinCoach", "tokens")
                    val hcManager = HealthConnectManager(applicationContext)
                    var doc = couchbaseController.findFirstDoc()

                    if (doc != null) {
                        var exerciseToken = doc.getString("exerciseToken")
                        var sleepToken = doc.getString("sleepToken")
                        var stepsToken = doc.getString("stepsToken")

                        if (exerciseToken != null) processExerciseChanges(exerciseToken)
                        if (sleepToken != null) processSleepChanges(sleepToken)
                        if (stepsToken != null) processStepsChanges(stepsToken)
                    } else {
                        //We can assume it is the first time it executes
                        //We will read all the records in Health Connect and process them
                        doc = MutableDocument()
                    }
                    doc.setString("exerciseToken", hcManager.getChangesToken(ExerciseSessionRecord::class))
                    doc.setString("sleepToken", hcManager.getChangesToken(SleepSessionRecord::class))
                    doc.setString("stepsToken", hcManager.getChangesToken(StepsRecord::class))
                    couchbaseController.saveDoc(doc)

                    if (!dcm.existsDittoThing(dcm.generateDittoThingId(Instant.now()))) {
                        //We assume that if after reading the new exercise sessions, there is not a
                        //Ditto thing for today, it is rest day
                        //TODO: Insert empty Ditto Thing
                    }

                    dcm.deleteDittoThing(dcm.generateDittoThingId(LocalDate.now().minusDays(System.getProperty("DITTO_THING_PERSISTENCE").toLong())))
                }
            } catch (e: Exception) {
                Log.i("onStartJob", e.toString())
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private suspend fun processExerciseChanges(token: String) {
        hcManager.getChanges(token).forEach {
            when (it) {
                is UpsertionChange -> {
                    val record = it.record as ExerciseSessionRecord
                    if(record.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING ||
                        record.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL) {
                        var laps = hcManager.processData(record)
                        var trainingSessionProperties = hcManager.rateTrainingSession(laps)

                        var dittoThing: DittoThing
                        try {
                            dittoThing = Gson().fromJson(dcm.getDittoThing(dcm.generateDittoThingId(record.startTime)), DittoThing::class.java)
                            dittoThing.features.trainingSession.properties = trainingSessionProperties
                        } catch (e: Exception) {
                            dittoThing = createRestDittoThing()
                            dittoThing.features.trainingSession.properties = trainingSessionProperties
                        }
                        dcm.putDittoThing(dcm.generateDittoThingId(record.startTime), Gson().toJson(dittoThing))
                    } else {
                        //TODO: Add some fatigue
                    }
                }
            }
        }
    }

    private suspend fun processSleepChanges(token: String) {
        hcManager.getChanges(token).forEach {
            when (it) {
                is UpsertionChange -> {
                    val record = it.record as SleepSessionRecord
                    //TODO
                }
            }
        }
    }

    private suspend fun processStepsChanges(token: String) {
        hcManager.getChanges(token).forEach {
            when (it) {
                is UpsertionChange -> {
                    val record = it.record as StepsRecord
                    //TODO
                }
            }
        }
    }
}