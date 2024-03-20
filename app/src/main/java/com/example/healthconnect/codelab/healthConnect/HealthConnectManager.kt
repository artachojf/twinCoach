package com.example.healthconnect.codelab.healthConnect

import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import com.example.healthconnect.codelab.dittoManager.DittoCurrentState
import java.io.IOException
import java.time.Instant
import kotlin.reflect.KClass

/**
 * Class in charge of the communication with Health Connect records
 */
class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }
    private val MONTH_LENGTH_SECONDS: Long = 30*24*60*60
    private val THRESHOLD_RESTING_PACE = 1.25
    val permissions = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    /**
     * This function checks if the application has been granted all the permissions requested
     */
    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    fun requestPermissionActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    /**
     * Retrieves all the exercise sessions between two timestamps
     * @param start
     * @param end
     * @return returns all the exercise sessions between those two timestamps. In case no session
     * is found returns an empty list
     */
    suspend fun readExerciseSessionRecords(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        try {
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            return response.records
        } catch (e: Exception) {
            Log.e("readExerciseSessionRecord", e.stackTraceToString())
            return emptyList()
        }
    }

    /**
     * Retrieves all the exercise sessions existing
     */
    suspend fun readExerciseSessionRecords(): List<ExerciseSessionRecord> {
        return readExerciseSessionRecords(Instant.now().minusSeconds(MONTH_LENGTH_SECONDS), Instant.now())
    }

    /**
     * Retrieves all the heart rate records between two timestamps
     * @param start
     * @param end
     * @return returns a list with all the heart rate records. In case no one is found returns
     * an empty list
     */
    suspend fun readHeartRateRecords(start: Instant, end: Instant): List<HeartRateRecord> {
        try {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            return response.records
        } catch (e: Exception) {
            Log.e("readExerciseSessionRecord", e.stackTraceToString())
            return emptyList()
        }
    }

    /**
     * Retrieves all the heart rate records existing
     */
    suspend fun readHeartRateRecords(): List<HeartRateRecord> {
        return readHeartRateRecords(Instant.now().minusSeconds(MONTH_LENGTH_SECONDS), Instant.now())
    }

    /**
     * Retrieve all the heart rate records during an exercise session
     * @param session
     */
    suspend fun readHeartRateRecords(session: ExerciseSessionRecord): List<HeartRateRecord> {
        return readHeartRateRecords(session.startTime, session.endTime)
    }

    /**
     * Retrieve all the speed records between two timestamps
     * @param start
     * @param end
     * @return returns a list with all the speed records found between those two timestamps.
     * In case no record is found returns an empty list
     */
    suspend fun readSpeedRecords(start: Instant, end: Instant): List<SpeedRecord> {
        try {
            val request = ReadRecordsRequest(
                recordType = SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            return response.records
        } catch (e: Exception) {
            Log.e("readExerciseSessionRecord", e.stackTraceToString())
            return emptyList()
        }
    }

    /**
     * Retrieve all the speed records of the previous 30 days
     */
    suspend fun readSpeedRecords(): List<SpeedRecord> {
        return readSpeedRecords(Instant.now().minusSeconds(MONTH_LENGTH_SECONDS), Instant.now())
    }

    /**
     * Retrieve all the speed records during an exercise session
     * @param session
     */
    suspend fun readSpeedRecords(session: ExerciseSessionRecord): List<SpeedRecord> {
        return readSpeedRecords(session.startTime.minusSeconds(120), session.endTime.plusSeconds(120))
    }

    /**
     * Obtains a Changes token for the specified record types.
     */
    suspend fun <T : Record> getChangesToken(recordClass: KClass<T>): String {
        return healthConnectClient.getChangesToken(
            ChangesTokenRequest(
                setOf(recordClass)
            )
        )
    }

    /**
     * Retrieve changes from a Changes token
     */
    suspend fun getChanges(token: String): List<Change> {
        var nextChangesToken = token
        var changesList = ArrayList<Change>()
        do {
            val response = healthConnectClient.getChanges(nextChangesToken)
            if (response.changesTokenExpired) {
                throw IOException("Changes token has expired")
            }
            changesList.addAll(response.changes)
            nextChangesToken = response.nextChangesToken
        } while (response.hasMore)

        return changesList
    }

    /**
     * Takes an ExerciseSessionRecord a extracts the laps made during the session
     * @param session
     * @return The list of laps/intervals done during the session. In case no speed record
     * is found in Health Connect returns an empty list
     */
    private suspend fun getLaps(session: ExerciseSessionRecord): List<ExerciseLap> {
        val speedRecord = this.readSpeedRecords(session)
        if (speedRecord.isEmpty()) return ArrayList()
        else return getLaps(speedRecord[0].samples)
    }

    /**
     * Takes a list of speeds during the time and calculates the laps/intervals done
     * during the exercise session
     * @param speedRecord List of speeds during time
     * @return The list of laps/intervals done during the session. In case speedRecord is
     * empty it returns an empty list
     */
    private fun getLaps(speedRecord: List<SpeedRecord.Sample>): List<ExerciseLap> {
        if (speedRecord.isEmpty()) return ArrayList()

        val laps = ArrayList<ExerciseLap>()
        var lapLength = 0.0
        var startTime = speedRecord[0].time
        for(i in 1..<speedRecord.size) {
            val timeOffset = speedRecord[i].time.epochSecond - speedRecord[i-1].time.epochSecond
            lapLength += speedRecord[i-1].speed.inMetersPerSecond*timeOffset

            if (hasStopped(speedRecord[i-1], speedRecord[i]) || hasStarted(speedRecord[i-1], speedRecord[i])) {
                val lap = ExerciseLap(startTime, speedRecord[i].time, Length.meters(lapLength))
                laps.add(lap)
                lapLength = 0.0
                startTime = speedRecord[i].time
            }
        }
        if(startTime != speedRecord.last().time) {
            val lap = ExerciseLap(startTime, speedRecord.last().time, Length.meters(lapLength))
            laps.add(lap)
        }
        return laps
    }

    /**
     * We will say the athlete has stopped a lap if it had a relatively high
     * speed and drops down the pace consistently
     */
    private fun hasStopped(prev: SpeedRecord.Sample, current: SpeedRecord.Sample): Boolean {
        return prev.speed.inMetersPerSecond.compareTo(THRESHOLD_RESTING_PACE) >= 0 &&
                prev.speed.inMetersPerSecond.compareTo(current.speed.inMetersPerSecond*2) > 0
    }

    /**
     * We will say the athlete has started a new lap if it now has a relatively high
     * speed and increases the pace consistently
     */
    private fun hasStarted(prev: SpeedRecord.Sample, current: SpeedRecord.Sample): Boolean {
        return current.speed.inMetersPerSecond.compareTo(THRESHOLD_RESTING_PACE) >= 0 &&
                prev.speed.inMetersPerSecond.compareTo(current.speed.inMetersPerSecond*0.5) < 0
    }

    /**
     * It takes a list of training session and rates it into four different categories:
     * strength, aerobic endurance, anaerobic endurance and fatigue.
     * @param session The training session to be evaluated
     * @return A TrainingSessionProperties object with the result. Returns null if
     * no speedRecord is associated to the session
     */
    suspend fun rateTrainingSession(session: ExerciseSessionRecord): DittoCurrentState.TrainingSessionProperties? {
        val laps = getLaps(session)
        if (laps.isEmpty()) return null

        val heartRateSamples = ArrayList<HeartRateRecord.Sample>()
        readHeartRateRecords(session).forEach {
            heartRateSamples.addAll(it.samples)
        }

        return if (heartRateSamples.isEmpty()) rateTrainingSession(laps)
        else rateTrainingSession(laps, heartRateSamples)
    }

    /**
     * Rates a training session only based on the laps/intervals made during the exercise
     * @param laps The laps/intervals made during the session
     * @return A TrainingSessionProperties object with the result
     */
    private fun rateTrainingSession(laps: List<ExerciseLap>): DittoCurrentState.TrainingSessionProperties {
        val result = DittoCurrentState.TrainingSessionProperties(0.0,0.0,0.0,0.0)
        return result
    }

    /**
     * Rates a training session based on the laps/intervals and the relative effort using a
     * five heart rate zones model
     * @param laps The laps/intervals made during the session
     * @param heartRates A list of heart rate samples during the session
     * @return A TrainingSessionProperties object with the result
     */
    private fun rateTrainingSession(laps: List<ExerciseLap>, heartRates: List<HeartRateRecord.Sample>): DittoCurrentState.TrainingSessionProperties {
        val result = DittoCurrentState.TrainingSessionProperties(0.0,0.0,0.0,0.0)
        //TODO: Retrieve max heart rate from Couchbase
        val maxHeartRate = 200
        var workoutTime = 0.0
        var restTime = 0.0

        laps.forEach {
            if (isWorkoutLap(it)) {
                val lapHeartRates = getLapHeartRates(it, heartRates)
                rateWorkoutLap(it, lapHeartRates, result, maxHeartRate)
                workoutTime += (it.endTime.epochSecond - it.startTime.epochSecond).toDouble()
            } else {
                restTime += (it.endTime.epochSecond - it.startTime.epochSecond).toDouble()
            }
        }
        val ratio = workoutTime/restTime
        if (ratio <= 5) {
            result.aerobic_endurance *= ratio
            result.anaerobic_endurance /= ratio
        }
        //TODO: Use world record

        return result
    }

    /**
     * Determines whether a lap/interval corresponds to a workout lap or a resting lap
     * @param lap
     * @return True if corresponds to a workout lap. False in case is resting
     */
    private fun isWorkoutLap(lap: ExerciseLap): Boolean {
        val seconds = lap.endTime.epochSecond - lap.startTime.epochSecond
        return (lap.length?.inMeters ?: 0.0) / seconds > THRESHOLD_RESTING_PACE
    }

    /**
     * Takes the list of all the heartRate samples of the workout and returns a sublist
     * of the samples corresponding to a specific lap
     * @param lap
     * @param heartRates
     * @return Sublist from heartRates param corresponding to the samples of the lap param
     */
    private fun getLapHeartRates(lap: ExerciseLap, heartRates: List<HeartRateRecord.Sample>): List<HeartRateRecord.Sample> {
        var startingIndex = 0
        var endingIndex = 0
        for (i in heartRates.indices) {
            if (startingIndex == 0 && heartRates[i].time >= lap.startTime) startingIndex = i
            else if (heartRates[i].time >= lap.endTime) {
                endingIndex = i
                break
            }
        }
        if (endingIndex == 0) endingIndex = heartRates.size
        return heartRates.subList(startingIndex, endingIndex)


    }

    /**
     * Takes all the needed information and rates the lap using a five heart rate zones model
     * @param lap The lap to be rated
     * @param lapHeartRates A list with the heart rate samples on this lap
     * @param state The object where the rating will be added
     * @param maxHeartRate The maximum heart rate of the athlete
     * @return It does not return anything. The rating is added to the corresponding
     * field of the parameter state
     */
    private fun rateWorkoutLap(lap: ExerciseLap, lapHeartRates: List<HeartRateRecord.Sample>,
                               state: DittoCurrentState.TrainingSessionProperties, maxHeartRate: Int) {
        for (i in lapHeartRates.indices) {
            val current = lapHeartRates[i]
            val next = if (i+1 == lapHeartRates.size) lap.endTime
            else lapHeartRates[i+1].time
            val offset = (next.epochSecond - current.time.epochSecond)*100.0

            if (current.beatsPerMinute >= 0.5*maxHeartRate && current.beatsPerMinute < 0.6*maxHeartRate) {
                //Zone 1: max time aprox 5 hours
                state.aerobic_endurance += (offset / (5*60*60))
                state.strength += (offset / (5*60*60*10))
                state.fatigue += (offset / (5*60*60))
            } else if (current.beatsPerMinute >= 0.6*maxHeartRate && current.beatsPerMinute < 0.7*maxHeartRate) {
                //Zone 2: max time aprox 3 hours
                state.aerobic_endurance += (offset / (3*60*60))
                state.strength += (offset / (3*60*60*3))
                state.fatigue += (offset / (3*60*60))
            } else if (current.beatsPerMinute >= 0.7*maxHeartRate && current.beatsPerMinute < 0.8*maxHeartRate) {
                //Zone 3: max time aprox 90 minutes
                state.aerobic_endurance += (offset / (90*60))
                state.anaerobic_endurance += (offset / (90*60*2))
                state.strength += (offset / (90*60*2))
                state.fatigue += (offset / (90*60))
            } else if (current.beatsPerMinute >= 0.8*maxHeartRate && current.beatsPerMinute < 0.9*maxHeartRate) {
                //Zone 4: max time aprox 50 minutes
                state.aerobic_endurance += (offset / (50*60*2))
                state.anaerobic_endurance += (offset / (50*60))
                state.strength += (offset / (50*60*2))
                state.fatigue += (offset / (50*60))
            } else {
                //Zone 5: max time aprox 20 minutes
                state.anaerobic_endurance += (offset / (20*60))
                state.strength += (offset / (20*60*2))
                state.fatigue += (offset / (20*60))
            }
        }
    }
}