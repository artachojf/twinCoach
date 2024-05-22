package com.example.healthconnect.codelab.ui.userPreferences

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetPreferences
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutPreferences
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutPreferencesParams
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val readGoogleId: ReadGoogleId,
    private val getPreferences: GetPreferences,
    private val putPreferences: PutPreferences
) : ViewModel() {

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _userPreferences = MutableLiveData<DittoGeneralInfo.Preferences?>()
    val userPreferences get() = _userPreferences

    private var _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess get() = _updateSuccess

    private var _error = MutableLiveData<ResponseFailure>()
    val error get() = _error

    fun init() {
        readGoogleId()
    }

    private fun readGoogleId() {
        viewModelScope.launch {
            readGoogleId(Unit) {
                it.fold(::handleGoogleIdFailure, ::handleGoogleIdSuccess)
            }
        }
    }

    private fun handleGoogleIdFailure(unit: Unit) {
        _error.postValue(ResponseFailure.UnknownError)
    }

    private fun handleGoogleIdSuccess(googleId: Flow<String>) {
        viewModelScope.launch {
            googleId.collect {
                getUserPreferences(it)
                _googleId.postValue(it)
            }
        }
    }

    private fun getUserPreferences(googleId: String) {
        viewModelScope.launch {
            getPreferences(googleId) {
                it.fold(::handleUserPreferencesFailure, ::handleUserPreferencesSuccess)
            }
        }
    }

    private fun handleUserPreferencesFailure(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleUserPreferencesSuccess(preferences: DittoGeneralInfo.Preferences?) {
        _userPreferences.postValue(preferences)
    }

    fun updateUserPreferences(googleId: String, preferences: DittoGeneralInfo.Preferences) {
        viewModelScope.launch {
            putPreferences(
                PutPreferencesParams(googleId, preferences)
            ) {
                it.fold(::handleUserPreferencesFailure, ::handleUpdateSuccess)
            }
        }
    }

    private fun handleUpdateFailure(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handleUpdateSuccess(unit: Unit) {
        _updateSuccess.postValue(true)
    }
}