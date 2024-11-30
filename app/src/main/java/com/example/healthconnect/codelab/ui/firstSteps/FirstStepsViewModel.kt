package com.example.healthconnect.codelab.ui.firstSteps

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGeneralInfoThing
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGeneralInfoThing
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadUserInformation
import com.example.healthconnect.codelab.utils.healthConnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstStepsViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val readGoogleId: ReadGoogleId,
    private val putGeneralInfoThing: PutGeneralInfoThing
) : ViewModel() {

    private var _sdkSupported = MutableLiveData<Boolean>()
    val sdkSupported get() = _sdkSupported

    private var _installed = MutableLiveData<Boolean>()
    val installed get() = _installed

    private var _hasPermission = MutableLiveData<Boolean>()
    val hasPermission get() = _hasPermission

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess get() = _updateSuccess

    private var _error = MutableLiveData<ResponseFailure>()
    val error get() = _error

    fun initFirst(context: Context) {
        _sdkSupported.postValue(HealthConnectManager.isSupported(context))
        _installed.postValue(HealthConnectManager.isInstalled(context))
    }

    fun initSecond() {
        viewModelScope.launch {
            _hasPermission.postValue(healthConnectManager.hasAllPermissions())
        }
    }

    fun initThird() {
        getGoogleId()
    }

    private fun getGoogleId() {
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
                _googleId.postValue(it)
            }
        }
    }

    fun putGeneralInfo(thing: DittoGeneralInfo.Thing) {
        viewModelScope.launch {
            putGeneralInfoThing(thing) {
                it.fold(::handleUpdateFailure, ::handleUpdateSuccess)
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