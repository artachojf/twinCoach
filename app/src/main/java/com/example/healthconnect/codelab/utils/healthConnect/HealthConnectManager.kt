package com.example.healthconnect.codelab.utils.healthConnect

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
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 * Class in charge of the communication with Health Connect records
 */
class HealthConnectManager @Inject constructor(
    val context: Context
) {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    private val THRESHOLD_WALKING_PACE_MS = 2.0
    private val MIN_SPEED_DROP = 1.0
    private val ZONE1 = 0.50
    private val ZONE2 = 0.75
    private val ZONE3 = 0.82
    val permissions = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )

    companion object {
        fun isSupported(context: Context): Boolean {
            return HealthConnectClient.getSdkStatus(context) != HealthConnectClient.SDK_UNAVAILABLE
        }

        fun isInstalled(context: Context): Boolean {
            return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        }
    }

    /**
     * This function checks if the application has been granted all the permissions requested
     */
    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
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
    suspend fun readExerciseSessionRecords(
        start: Instant,
        end: Instant
    ): List<ExerciseSessionRecord> {
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
    suspend fun readAllExerciseSessionRecords(): List<ExerciseSessionRecord> {
        try {
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.before(Instant.now())
            )
            val response = healthConnectClient.readRecords(request)
            return response.records
        } catch (e: Exception) {
            Log.e("readExerciseSessionRecord", e.stackTraceToString())
            return emptyList()
        }
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
     * Retrieve all the speed records during an exercise session
     * @param session
     */
    suspend fun readSpeedRecords(session: ExerciseSessionRecord): List<SpeedRecord> {
        return readSpeedRecords(
            session.startTime.minusSeconds(120),
            session.endTime.plusSeconds(120)
        )
    }

    suspend fun readAllSleepRecords(): List<SleepSessionRecord> {
        return try {
            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.before(Instant.now())
            )
            val response = healthConnectClient.readRecords(request)
            response.records
        } catch (e: Exception) {
            Log.e("readSleepSessionRecord", e.stackTraceToString())
            emptyList()
        }
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
    suspend fun rateTrainingSession(
        session: ExerciseSessionRecord,
        birthdate: LocalDate
    ): DittoCurrentState.TrainingSession? {

        val speedSamples = ArrayList<SpeedRecord.Sample>()
        readSpeedRecords(session).forEach {
            speedSamples.addAll(it.samples)
        }

        val heartRateSamples = ArrayList<HeartRateRecord.Sample>()
        readHeartRateRecords(session).forEach {
            heartRateSamples.addAll(it.samples)
        }

        return if (heartRateSamples.isEmpty() || speedSamples.isEmpty()) null
        else {
            val maxHeartRate = 220 - (LocalDate.now().year - birthdate.year)
            rateTrainingSession(speedSamples, heartRateSamples, maxHeartRate)
        }
    }

    /**
     * Decomposes a training session based on the speed samples and the relative
     * effort using a three heart rate zones model.
     * @param speeds The speed samples during the session
     * @param heartRates A list of heart rate samples during the session
     * @return A TrainingSessionProperties object with the result
     */
    private fun rateTrainingSession(
        speeds: List<SpeedRecord.Sample>,
        heartRates: List<HeartRateRecord.Sample>,
        maxHeartRate: Int
    ): DittoCurrentState.TrainingSession {

        val z1 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val z2 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val z3 = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val rest = DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0)
        val laps = mutableListOf<DittoCurrentState.TrainingLap>()

        for (i in speeds.indices) {
            if (i+1 == speeds.size) continue

            val s = speeds[i]
            val nextS = speeds[i+1]
            val offset = (nextS.time.epochSecond - s.time.epochSecond).toDouble()
            val h = heartRates.findLast { it.time <= s.time } ?: continue
            val distance = s.speed.inMetersPerSecond * offset

            updateHeartRateZones(s, offset, h, distance, maxHeartRate, z1, z2, z3, rest)
            updateLaps(s, nextS, laps)
        }

        return DittoCurrentState.TrainingSession(z1, z2, z3, rest, laps)
    }

    private fun updateHeartRateZones(
        s: SpeedRecord.Sample,
        offset: Double,
        h: HeartRateRecord.Sample,
        distance: Double,
        maxHeartRate: Int,
        z1: DittoCurrentState.TrainingSessionZone,
        z2: DittoCurrentState.TrainingSessionZone,
        z3: DittoCurrentState.TrainingSessionZone,
        rest: DittoCurrentState.TrainingSessionZone,
    ) {
        if (s.speed.inMetersPerSecond > THRESHOLD_WALKING_PACE_MS) {
            val maxHrPercentage = h.beatsPerMinute.toDouble() / maxHeartRate.toLong()

            if (maxHrPercentage >= ZONE1 && maxHrPercentage < ZONE2) {
                z1.combine(
                    h.beatsPerMinute.toDouble(),
                    offset,
                    distance
                )
            } else if (maxHrPercentage >= ZONE2 && maxHrPercentage < ZONE3) {
                z2.combine(
                    h.beatsPerMinute.toDouble(),
                    offset,
                    distance
                )
            } else if (maxHrPercentage >= ZONE3) {
                z3.combine(
                    h.beatsPerMinute.toDouble(),
                    offset,
                    distance
                )
            }
        } else {
            rest.combine(
                h.beatsPerMinute.toDouble(),
                offset,
                distance
            )
        }
    }

    private fun updateLaps(
        currentSpeed: SpeedRecord.Sample,
        nextSpeed: SpeedRecord.Sample,
        laps: MutableList<DittoCurrentState.TrainingLap>
    ) {
        if (laps.isEmpty() ||
            abs(currentSpeed.speed.inMetersPerSecond - nextSpeed.speed.inMetersPerSecond) >= MIN_SPEED_DROP)
            laps.add(
                DittoCurrentState.TrainingLap(
                    LocalDateTime.ofInstant(currentSpeed.time, ZoneId.systemDefault()),
                    0.0,
                    0.0
                )
            )

        val currentLap = laps[laps.size-1]
        val timeOffset = (nextSpeed.time.epochSecond - currentSpeed.time.epochSecond).toDouble()
        val distanceOffset = currentSpeed.speed.inMetersPerSecond * timeOffset
        currentLap.time += timeOffset
        currentLap.distance += distanceOffset
    }

    fun rateStepsRecord(record: StepsRecord): DittoCurrentState.StepsRecord {
        return DittoCurrentState.StepsRecord(record.count.toInt())
    }

    fun rateSleepSession(record: SleepSessionRecord): DittoCurrentState.SleepSession {
        val session = DittoCurrentState.SleepSession(0,0,0,0)

        record.stages.forEach {
            val offset = it.endTime.epochSecond - it.startTime.epochSecond
            when (it.stage) {
                SleepSessionRecord.STAGE_TYPE_AWAKE -> session.awake += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> session.awake += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> session.awake += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_LIGHT -> session.light += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_SLEEPING -> session.light += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_DEEP -> session.deep += offset.toInt()
                SleepSessionRecord.STAGE_TYPE_REM -> session.rem += offset.toInt()
                else -> session.awake += offset.toInt()
            }
        }

        return session
    }
}