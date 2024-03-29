package com.example.healthconnect.codelab.healthConnect

import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
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
    private val THRESHOLD_WALKING_PACE_MS = 1.25
    private val ZONE1 = 50
    private val ZONE2 = 75
    private val ZONE3 = 82
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
     * It takes a training session and decomposes training session in
     * the time spent in each HR zone using a three heart rate zones model.
     * @param session The training session to be evaluated
     * @return A TrainingSessionProperties object with the result. Returns null if
     * no speedRecord or heartRateRecord is associated to the session
     */
    suspend fun rateTrainingSession(session: ExerciseSessionRecord): DittoCurrentState.TrainingSessionProperties? {
        val speedSamples = ArrayList<SpeedRecord.Sample>()
        readSpeedRecords(session).forEach {
            speedSamples.addAll(it.samples)
        }

        val heartRateSamples = ArrayList<HeartRateRecord.Sample>()
        readHeartRateRecords(session).forEach {
            heartRateSamples.addAll(it.samples)
        }

        return if (heartRateSamples.isEmpty() || speedSamples.isEmpty()) null
        else rateTrainingSession(speedSamples, heartRateSamples)
    }

    /**
     * Decomposes a training session based on the speed samples and the relative
     * effort using a three heart rate zones model.
     * @param speeds The speed samples during the session
     * @param heartRates A list of heart rate samples during the session
     * @return A TrainingSessionProperties object with the result
     */
    private fun rateTrainingSession(speeds: List<SpeedRecord.Sample>,
                                    heartRates: List<HeartRateRecord.Sample>)
    : DittoCurrentState.TrainingSessionProperties {
        val z1 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val z2 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val z3 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val rest = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        //TODO: Retrieve max heart rate from Couchbase
        val maxHeartRate = 200

        for(i in speeds.indices) {
            val s = speeds[i]
            val offset = (speeds[i+1].time.epochSecond - s.time.epochSecond)*100.0
            val h = heartRates.findLast {
                it.time <= s.time
            } ?: continue

            if (s.speed.inMetersPerSecond > THRESHOLD_WALKING_PACE_MS) {
                val maxHrPercentage = h.beatsPerMinute.toDouble() / maxHeartRate.toLong()

                if (maxHrPercentage >= ZONE1 && maxHrPercentage < ZONE2) {
                    z1.combine(h.beatsPerMinute.toDouble(), offset, s.speed.inMetersPerSecond*offset)
                } else if (maxHrPercentage >= ZONE2 && maxHrPercentage < ZONE3) {
                    z2.combine(h.beatsPerMinute.toDouble(), offset, s.speed.inMetersPerSecond*offset)
                } else if (maxHrPercentage >= ZONE3) {
                    z3.combine(h.beatsPerMinute.toDouble(), offset, s.speed.inMetersPerSecond*offset)
                }
            } else {
                rest.combine(h.beatsPerMinute.toDouble(), offset, s.speed.inMetersPerSecond*offset)
            }
        }

        return DittoCurrentState.TrainingSessionProperties(z1, z2, z3, rest)
    }
}