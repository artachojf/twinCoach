package com.example.healthconnect.codelab.ui.sessions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState.GetAllCurrentStateThings
import com.example.healthconnect.codelab.domain.usecase.ditto.get.suggestedSessions.GetSuggestedSessions
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val getAllCurrentStateThings: GetAllCurrentStateThings,
    private val readGoogleId: ReadGoogleId,
    private val getSuggestedSessions: GetSuggestedSessions
) : ViewModel() {

    private var _sessions = MutableLiveData<List<DittoCurrentState.Thing>>()
    val sessions get() = _sessions

    private var _suggestedSessions = MutableLiveData<List<DittoGeneralInfo.TrainingSession?>>()
    val suggestedSessions get() = _suggestedSessions

    private var _error = MutableLiveData<DittoError>()
    val error get() = _error

    var calendarDate = LocalDate.now()

    fun init() {
        getGoogleId()
    }

    private fun getGoogleId() {
        viewModelScope.launch {
            readGoogleId(Unit) {
                it.fold(::handleGoogleIdError, ::handleGoogleIdSuccess)
            }
        }
    }

    private fun handleGoogleIdError(unit: Unit) {
        _error.postValue(DittoError(ResponseFailure.UnknownError))
    }

    private fun handleGoogleIdSuccess(googleId: Flow<String>) {
        viewModelScope.launch {
            googleId.collect {
                getSessions(it)
                getSuggestedSessions(it)
            }
        }
    }

    fun getSessions(googleId: String) {
        viewModelScope.launch {
            getAllCurrentStateThings(googleId) {
                it.fold(::handleSessionsError, ::handleSessionsSuccess)
            }
        }
    }

    private fun handleSessionsError(error: DittoError) {
        _error.postValue(error)
    }

    private fun handleSessionsSuccess(sessions: List<DittoCurrentState.Thing>) {
        _sessions.postValue(sessions.sortedBy { it.attributes.date })
    }

    fun getSuggestedSessions(googleId: String) {
        viewModelScope.launch {
            getSuggestedSessions(googleId) {
                it.fold(::handleSuggestionsError, ::handleSuggestionsSuccess)
            }
        }
    }

    private fun handleSuggestionsError(error: DittoError) {
        _error.postValue(error)
    }

    private fun handleSuggestionsSuccess(suggestions: DittoGeneralInfo.TrainingPlan?) {
        _suggestedSessions.postValue(suggestions?.sessions)
    }
}