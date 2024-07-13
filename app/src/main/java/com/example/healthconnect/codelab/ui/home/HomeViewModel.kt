package com.example.healthconnect.codelab.ui.home

import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.model.token.Token
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState.GetCurrentStateInput
import com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState.GetCurrentStateThing
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGeneralInfoThing
import com.example.healthconnect.codelab.domain.usecase.ditto.put.currentState.PutCurrentStateThing
import com.example.healthconnect.codelab.domain.usecase.token.ReadTokens
import com.example.healthconnect.codelab.domain.usecase.token.WriteTokens
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadUserInformation
import com.example.healthconnect.codelab.utils.healthConnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val retrieveGeneralInfo: GetGeneralInfoThing,
    private val readUserInfo: ReadUserInformation,
    private val healthConnectManager: HealthConnectManager,
    private val readTokens: ReadTokens,
    private val writeTokens: WriteTokens,
    private val getCurrentStateThing: GetCurrentStateThing,
    private val putCurrentStateThing: PutCurrentStateThing
) : ViewModel() {

    private var _generalInfo = MutableLiveData<DittoGeneralInfo.Thing?>()
    val generalInfo get() = _generalInfo

    private var _error = MutableLiveData<DittoError>()
    val error get() = _error

    private var _healthConnectPermission = MutableLiveData<Boolean>()
    val healthConnectPermission get() = _healthConnectPermission

    private var _userInformation = MutableLiveData<UserInformation>()
    val userInformation get() = _userInformation

    private var _healthConnectError = MutableLiveData<Boolean>()
    val healthConnectError get() = _healthConnectError

    private var _updateFinished = MutableLiveData<Boolean>()
    val updateFinished get() = _updateFinished

    private var newExerciseSession = false
    private var newSleepsSession = false
    private var newStepsRecord = false

    fun init() {
        getUserInformation()
    }

    private fun getGeneralInfo(googleId: String) {
        viewModelScope.launch {
            retrieveGeneralInfo(googleId) {
                it.fold(::handleGeneralInfoError, ::handleGeneralInfoSuccess)
            }
        }
    }

    private fun handleGeneralInfoError(error: DittoError) {
        _error.postValue(error)
    }

    private fun handleGeneralInfoSuccess(info: DittoGeneralInfo.Thing?) {
        _generalInfo.postValue(info)
    }

    private fun getUserInformation() {
        viewModelScope.launch {
            readUserInfo(Unit) {
                it.fold(::handleUserInformationError, ::handleUserInformationSuccess)
            }
        }
    }

    private fun handleUserInformationError(unit: Unit) {
        _error.postValue(DittoError(ResponseFailure.UnknownError))
    }

    private fun handleUserInformationSuccess(userInfo: Flow<UserInformation>) {
        viewModelScope.launch {
            userInfo.take(1).collect {
                _userInformation.postValue(it)
                getGeneralInfo(it.googleId)
            }
        }
    }

    fun healthConnectPermissions() {
        viewModelScope.launch {
            _healthConnectPermission.postValue(healthConnectManager.hasAllPermissions())
        }
    }

    fun updateHealthConnectData() {
        readTokens()
    }

    private fun readTokens() {
        viewModelScope.launch {
            readTokens(Unit) {
                it.fold(::handleHealthConnectError, ::handleTokensSuccess)
            }
        }
    }

    private fun handleHealthConnectError(unit: Unit) {
        _healthConnectError.postValue(true)
    }

    private fun handleTokensSuccess(tokens: Flow<Token>) {
        viewModelScope.launch {
            tokens.take(1).collect {
                if (it.exerciseSessionToken.isEmpty()) {
                    readAllRecords(it, userInformation.value!!)
                } else {
                    processChanges(it, userInformation.value!!)
                }
            }
        }
    }

    private fun handleCurrentStateFailure(error: DittoError) {
        _healthConnectError.postValue(true)
    }

    private fun handleGetCurrentStateSuccess(
        resultThing: DittoCurrentState.Thing?,
        bufferedThing: DittoCurrentState.Thing
    ) {
        var putThing = resultThing
        if (resultThing == null) {
            putThing = bufferedThing
        } else {
            putThing!!.combine(bufferedThing)
        }

        viewModelScope.launch {
            putCurrentStateThing(putThing) {
                it.fold(::handleCurrentStateFailure, {})
            }
        }
    }

    private fun renewTokens(previousToken: Token) {
        viewModelScope.launch {
            val newTokens = Token(
                if (newExerciseSession) healthConnectManager.getChangesToken(ExerciseSessionRecord::class) else previousToken.exerciseSessionToken,
                if (newSleepsSession) healthConnectManager.getChangesToken(SleepSessionRecord::class) else previousToken.sleepToken,
                if (newStepsRecord) healthConnectManager.getChangesToken(StepsRecord::class) else previousToken.stepsToken
            )

            writeTokens(newTokens) {
                it.fold(::handleHealthConnectError, ::handleUpdateFinished)
            }
        }
    }

    private fun handleUpdateFinished(unit: Unit) {
        _updateFinished.postValue(newExerciseSession || newSleepsSession || newStepsRecord)
        newExerciseSession = false
        newSleepsSession = false
        newStepsRecord = false
    }

    private fun readAllRecords(token: Token, info: UserInformation) {
        viewModelScope.launch {
            healthConnectManager.readAllExerciseSessionRecords().forEach {
                async { processSession(it, info) }.await()
            }
            newExerciseSession = true
            newSleepsSession = true
            newStepsRecord = true

            renewTokens(token)
        }
    }

    private suspend fun processChanges(token: Token, info: UserInformation) {
        token.toList().forEach {
            when (val result = healthConnectManager.getChanges(it)) {
                is HealthConnectManager.GetChangesResult.Success -> {
                    processChangesSuccess(result.changes, info)
                }

                is HealthConnectManager.GetChangesResult.Error -> {
                    readAllRecords(token, info)
                }
            }
        }
        renewTokens(token)
    }

    private suspend fun processChangesSuccess(changes: List<Change>, info: UserInformation) {
        viewModelScope.launch {
            changes.forEach { change ->
                async {
                    when (change) {
                        is UpsertionChange -> {
                            when (change.record) {
                                is ExerciseSessionRecord -> {
                                    processSession(
                                        change.record as ExerciseSessionRecord,
                                        info
                                    )
                                    newExerciseSession = true
                                }

                                is StepsRecord -> {
                                    processSteps(
                                        change.record as StepsRecord,
                                        info
                                    )
                                    newStepsRecord = true
                                }

                                is SleepSessionRecord -> {
                                    processSleep(
                                        change.record as SleepSessionRecord,
                                        info
                                    )
                                    newSleepsSession = true
                                }

                                else -> {}
                            }
                        }

                        else -> {}
                    }
                }.await()
            }
        }
    }

    private suspend fun uploadThing(
        thing: DittoCurrentState.Thing
    ) {
        getCurrentStateThing(
            GetCurrentStateInput(
                thing.attributes.googleId,
                thing.attributes.date.toLocalDate()
            )
        ) { result ->
            result.fold(
                ::handleCurrentStateFailure
            ) {
                handleGetCurrentStateSuccess(
                    it,
                    thing
                )
            }
        }
    }

    private suspend fun processSession(
        record: ExerciseSessionRecord,
        info: UserInformation
    ) {
        healthConnectManager.rateTrainingSession(
            record,
            generalInfo.value!!.attributes.birthdate
        ).let { session ->
            if (session != null) {
                uploadThing(newThing(session, info.googleId, record.startTime))
            }
        }
    }

    private suspend fun processSteps(
        stepsRecord: StepsRecord,
        info: UserInformation
    ) {
        val record = healthConnectManager.rateStepsRecord(stepsRecord)
        uploadThing(newThing(record, info.googleId, stepsRecord.startTime))
    }

    private suspend fun processSleep(
        sleepRecord: SleepSessionRecord,
        info: UserInformation
    ) {
        val session = healthConnectManager.rateSleepSession(sleepRecord)
        uploadThing(newThing(session, info.googleId, sleepRecord.startTime))
    }

    private fun newThing(
        session: DittoCurrentState.TrainingSession,
        googleId: String,
        time: Instant
    ): DittoCurrentState.Thing {
        val attributes = DittoCurrentState.Attributes(
            googleId,
            LocalDateTime.ofInstant(time, ZoneId.systemDefault())
        )

        val features = DittoCurrentState.Features(
            session,
            DittoCurrentState.SleepSession(0, 0, 0, 0),
            DittoCurrentState.StepsRecord(0)
        )

        return DittoCurrentState.Thing(
            thingId = DittoCurrentState.thingId(googleId, time),
            attributes = attributes,
            features = features
        )
    }

    private fun newThing(
        record: DittoCurrentState.StepsRecord,
        googleId: String,
        time: Instant
    ): DittoCurrentState.Thing {
        val attributes = DittoCurrentState.Attributes(
            googleId,
            LocalDateTime.ofInstant(time, ZoneId.systemDefault())
        )

        val session = DittoCurrentState.TrainingSession(
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            mutableListOf()
        )

        val features = DittoCurrentState.Features(
            session,
            DittoCurrentState.SleepSession(0, 0, 0, 0),
            record
        )

        return DittoCurrentState.Thing(
            thingId = DittoCurrentState.thingId(googleId, time),
            attributes = attributes,
            features = features
        )
    }

    private fun newThing(
        session: DittoCurrentState.SleepSession,
        googleId: String,
        time: Instant
    ): DittoCurrentState.Thing {
        val attributes = DittoCurrentState.Attributes(
            googleId,
            LocalDateTime.ofInstant(time, ZoneId.systemDefault())
        )

        val trainingSession = DittoCurrentState.TrainingSession(
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            DittoCurrentState.TrainingSessionZone(0.0, 0.0, 0.0),
            mutableListOf()
        )

        val features = DittoCurrentState.Features(
            trainingSession,
            session,
            DittoCurrentState.StepsRecord(0)
        )

        return DittoCurrentState.Thing(
            thingId = DittoCurrentState.thingId(googleId, time),
            attributes = attributes,
            features = features
        )
    }
}