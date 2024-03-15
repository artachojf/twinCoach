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
            println(e.toString())
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
            println(e.toString())
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
            println(e.toString())
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
     * @return The list of laps/intervals done during the session
     */
    suspend fun getLaps(session: ExerciseSessionRecord): List<ExerciseLap> {
        val speedRecord = this.readSpeedRecords(session)[0].samples
        return getLaps(speedRecord)
    }

    /**
     * Takes a list of speeds during the time and calculates the laps/intervals done
     * during the exercise session
     * @param speedRecord List of speeds during time
     * @return The list of laps/intervals done during the session
     */
    private fun getLaps(speedRecord: List<SpeedRecord.Sample>): List<ExerciseLap> {
        val laps = ArrayList<ExerciseLap>()
        var lapLength = 0.0
        var startTime = speedRecord[0].time
        for(i in 1..speedRecord.size-1) {
            val timeOffset = speedRecord[i].time.epochSecond - speedRecord[i-1].time.epochSecond
            lapLength += speedRecord[i-1].speed.inMetersPerSecond*timeOffset

            if (hasStopped(speedRecord[i-1], speedRecord[i]) || hasStopped(speedRecord[i-1], speedRecord[i])) {
                val lap = ExerciseLap(startTime, speedRecord[i].time, Length.meters(lapLength))
                laps.add(lap)
                lapLength = 0.0
                startTime = speedRecord[i].time
                Log.i("New lap", lap.length.toString())
            }
        }
        if(!startTime.equals(speedRecord.last().time)) {
            val lap = ExerciseLap(startTime, speedRecord.last().time, Length.meters(lapLength))
            laps.add(lap)
            Log.i("New lap", lap.length.toString())
        }
        return laps
    }

    /**
     * We will say the athlete has stopped a lap if it had a relatively high
     * speed and drops down the pace consistently
     */
    private fun hasStopped(prev: SpeedRecord.Sample, current: SpeedRecord.Sample): Boolean {
        return prev.speed.inMetersPerSecond.compareTo(THRESHOLD_RESTING_PACE) > 1 &&
                prev.speed.inMetersPerSecond.compareTo(current.speed.inMetersPerSecond*2) > 0
    }

    /**
     * We will say the athlete has started a new lap if it now has a relatively high
     * speed and increases the pace consistently
     */
    private fun hasStarted(prev: SpeedRecord.Sample, current: SpeedRecord.Sample): Boolean {
        return current.speed.inMetersPerSecond.compareTo(THRESHOLD_RESTING_PACE) > 1 &&
                prev.speed.inMetersPerSecond.compareTo(current.speed.inMetersPerSecond*0.5) < 0
    }

    /**
     * It takes a list of laps/intervals and rates the session in four different categories:
     * strength, aerobic endurance, anaerobic endurance and fatigue.
     * @param laps The list of laps/intervals of the exercise session
     * @return A TrainingSessionProperties object with the result
     */
    fun rateTrainingSession(laps: List<ExerciseLap>): DittoCurrentState.TrainingSessionProperties {
        val result = DittoCurrentState.TrainingSessionProperties(0.0, 0.0, 0.0, 0.0)
        //TODO: Rate training sessions
        return result
    }
}