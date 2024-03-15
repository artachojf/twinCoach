package com.example.healthconnect.codelab.dittoManager

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import com.couchbase.lite.MutableDocument
import com.example.healthconnect.codelab.healthConnect.HealthConnectManager
import com.example.healthconnect.codelab.couchbase.CouchbaseController
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate


class PeriodicDittoService : JobService() {

    private lateinit var hcManager: HealthConnectManager
    private val dcm = DittoConnectionManager()

    /**
     * Creates a new job service that reads changes in Health Connect.
     * In case there are changes, they are processed and uploaded to Ditto server.
     * In case there are no changes and there is not a Thing for the current day it is created
     * a new empty Thing.
     * On the other hand, if it is the first time it executes, all the records in Health Connect are
     * read and uploaded.
     * After that, the Ditto Thing associated to (<DITTO_THING_PERSISTENCE> environment variable)
     * days before is deleted.
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    val couchbaseController = CouchbaseController(applicationContext, "twinCoach", "tokens")
                    val hcManager = HealthConnectManager(applicationContext)
                    var doc = couchbaseController.findFirstDoc()

                    if (doc != null) {
                        val exerciseToken = doc.getString("exerciseToken")
                        val sleepToken = doc.getString("sleepToken")
                        val stepsToken = doc.getString("stepsToken")

                        if (exerciseToken != null) processExerciseChanges(exerciseToken)
                        if (sleepToken != null) processSleepChanges(sleepToken)
                        if (stepsToken != null) processStepsChanges(stepsToken)
                    } else {
                        hcManager.readExerciseSessionRecords().forEach {
                            uploadToDitto(it)
                        }
                        doc = MutableDocument()
                    }
                    doc.setString("exerciseToken", hcManager.getChangesToken(ExerciseSessionRecord::class))
                    doc.setString("sleepToken", hcManager.getChangesToken(SleepSessionRecord::class))
                    doc.setString("stepsToken", hcManager.getChangesToken(StepsRecord::class))
                    couchbaseController.saveDoc(doc)

                    if (!dcm.existsDittoThing(DittoCurrentState().generateThingId(Instant.now()))) {
                        val thing = DittoCurrentState().createRestThing()
                        dcm.putDittoThing(DittoCurrentState().generateThingId(Instant.now()), Gson().toJson(thing))
                    }

                    dcm.deleteDittoThing(DittoCurrentState().generateThingId(LocalDate.now().minusDays(System.getProperty("DITTO_THING_PERSISTENCE").toLong())))
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

    /**
     * The exercise record is read and rated in each aspect.
     * After that, the Thing associated with the session's day is read.
     * In case it exists, the values are overwritten. In other case a new Thing is created
     * with the ratings calculated before.
     * @param record The exercise record to be uploaded
     */
    private suspend fun uploadToDitto(record: ExerciseSessionRecord) {
        val laps = hcManager.getLaps(record)
        val trainingSessionProperties = hcManager.rateTrainingSession(laps)

        var dittoThing: DittoCurrentState.Thing
        /**
         * We try to get the Ditto Thing associated to the training session in case it exists.
         * In the other case, we create a new thing with the ratings we just calculated
         */
        try {
            dittoThing = Gson().fromJson(dcm.getDittoThing(DittoCurrentState().generateThingId(record.startTime)), DittoCurrentState.Thing::class.java)
            dittoThing.features.trainingSession.properties = trainingSessionProperties
        } catch (e: Exception) {
            dittoThing = DittoCurrentState().createRestThing()
            dittoThing.features.trainingSession.properties = trainingSessionProperties
        }
        dcm.putDittoThing(DittoCurrentState().generateThingId(record.startTime), Gson().toJson(dittoThing))
    }

    private suspend fun processExerciseChanges(token: String) {
        hcManager.getChanges(token).forEach {
            when (it) {
                is UpsertionChange -> {
                    val record = it.record as ExerciseSessionRecord
                    if(record.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING ||
                        record.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL) {
                        uploadToDitto(record)
                    } else {
                        /**
                         * TODO: In case the exercise is not running, we won't rate the session,
                         * but we must add some fatigue to that day's information
                         */
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