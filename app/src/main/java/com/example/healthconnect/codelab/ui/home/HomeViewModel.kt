package com.example.healthconnect.codelab.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.RetrieveCurrentStateThing
import com.example.healthconnect.codelab.domain.usecase.ditto.RetrieveGeneralInfoThing
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val retrieveCurrentState: RetrieveCurrentStateThing,
    private val retrieveGeneralInfo: RetrieveGeneralInfoThing,
    private val readGoogleId: ReadGoogleId
) : ViewModel() {

    private var _currentState = MutableLiveData<DittoCurrentState.Thing>()
    val currentState get() = _currentState

    private var _generalInfo = MutableLiveData<DittoGeneralInfo.Thing>()
    val generalInfo get() = _generalInfo

    private var _error = MutableLiveData<Unit>()
    val error get() = _error

    fun init() {
        getGoogleId()
    }

    fun getGeneralInfo(googleId: String) {
        viewModelScope.launch {
            retrieveGeneralInfo(googleId) {
                it.fold(::handleGeneralInfoError, ::handleGeneralInfoSuccess)
            }
        }
    }

    private fun handleGeneralInfoError(unit: Unit) {
        _error.postValue(unit)
    }

    private fun handleGeneralInfoSuccess(info: DittoGeneralInfo.Thing) {
        _generalInfo.postValue(info)
    }

    fun getCurrentState(googleId: String) {
        viewModelScope.launch {
            retrieveCurrentState(googleId) {
                it.fold(::handleCurrentStateError, ::handleCurrentStateSuccess)
            }
        }
    }

    private fun handleCurrentStateError(unit: Unit) {
        _error.postValue(unit)
    }

    private fun handleCurrentStateSuccess(currentState: DittoCurrentState.Thing) {
        _currentState.postValue(currentState)
    }

    fun getGoogleId() {
        viewModelScope.launch {
            readGoogleId(Unit) {
                it.fold({}, ::handleGoogleIdSuccess)
            }
        }
    }

    private fun handleGoogleIdSuccess(googleId: Flow<String>) {
        viewModelScope.launch {
            googleId.collect {
                getGeneralInfo(it)
                getCurrentState(it)
            }
        }
    }
}