package com.example.healthconnect.codelab.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.delete.DeleteThing
import com.example.healthconnect.codelab.domain.usecase.ditto.get.currentState.GetAllCurrentStateThings
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAllCurrentStateThings: GetAllCurrentStateThings,
    private val readGoogleId: ReadGoogleId,
    private val deleteThing: DeleteThing
) : ViewModel() {

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _currentStates = MutableLiveData<List<DittoCurrentState.Thing>>()
    val currentStates get() = _currentStates

    private var _deletedThings = MutableLiveData(0)
    val deletedThings get() = _deletedThings

    private var _deletedAccount = MutableLiveData<Boolean>()
    val deletedAccount get() = _deletedAccount

    private var _error = MutableLiveData<ResponseFailure>()
    val error get() = _error

    fun initDelete() {
        viewModelScope.launch {
            readGoogleId(Unit) {
                it.fold(::handleIdFailure, ::handleIdSuccess)
            }
        }
    }

    private fun handleIdFailure(unit: Unit) {
        _error.postValue(ResponseFailure.UnknownError)
    }

    private fun handleIdSuccess(googleId: Flow<String>) {
        viewModelScope.launch {
            googleId.collect {
                _googleId.postValue(it)
                getCurrentStates(it)
            }
        }
    }

    private fun getCurrentStates(googleId: String) {
        viewModelScope.launch {
            getAllCurrentStateThings(googleId) {
                it.fold(::handleCurrentStatesFailure, ::handleCurrentStatesSuccess)
            }
        }
    }

    private fun handleCurrentStatesFailure(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleCurrentStatesSuccess(list: List<DittoCurrentState.Thing>) {
        _currentStates.postValue(list)

        list.forEach {
            deleteThing(it.thingId)
        }

        deleteThing(DittoGeneralInfo.thingId(googleId.value!!))
        _deletedAccount.postValue(true)
    }

    private fun deleteThing(thingId: String) {
        viewModelScope.launch {
            deleteThing(thingId) {
                it.fold(::handleDeleteFailure, ::handleDeleteSuccess)
            }
        }
    }

    private fun handleDeleteFailure(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleDeleteSuccess(unit: Unit) {
        _deletedThings.postValue((_deletedThings.value ?: 0) + 1)
    }
}