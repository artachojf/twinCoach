package com.example.healthconnect.codelab.ui.goal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGoalInformation
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGoal
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGoalParams
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val getGoalInformation: GetGoalInformation,
    private val readGoogleId: ReadGoogleId,
    private val putGoal: PutGoal
) : ViewModel() {

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _goal = MutableLiveData<DittoGeneralInfo.Goal?>()
    val goal get() = _goal

    private var _updateGoalSuccess = MutableLiveData<Boolean>()
    val updateGoalSuccess get() = _updateGoalSuccess

    private var _error = MutableLiveData<ResponseFailure>()
    val error get() = _error

    var date: LocalDate? = null

    fun init() {
        readGoogleId()
    }

    private fun readGoogleId() {
        viewModelScope.launch {
            readGoogleId(Unit) {
                it.fold(::handleGoogleIdError, ::handleGoogleIdSuccess)
            }
        }
    }

    private fun handleGoogleIdError(unit: Unit) {
        _error.postValue(ResponseFailure.UnknownError)
    }

    private fun handleGoogleIdSuccess(googleId: Flow<String>) {
        viewModelScope.launch {
            googleId.collect {
                getGoalInformation(it)
                _googleId.postValue(it)
            }
        }
    }

    private fun getGoalInformation(googleId: String) {
        viewModelScope.launch {
            getGoalInformation(googleId) {
                it.fold(::handleGoalInformationError, ::handleGoalInformationSuccess)
            }
        }
    }

    private fun handleGoalInformationError(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleGoalInformationSuccess(goal: DittoGeneralInfo.Goal?) {
        _goal.postValue(goal)
    }

    fun updateGoal(
        googleId: String,
        goal: DittoGeneralInfo.Goal
    ) {
        viewModelScope.launch {
            putGoal(PutGoalParams(googleId, goal)) {
                it.fold(::handleUpdateGoalError, ::handleUpdateGoalSuccess)
            }
        }
    }

    private fun handleUpdateGoalError(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleUpdateGoalSuccess(unit: Unit) {
        _updateGoalSuccess.postValue(true)
    }
}