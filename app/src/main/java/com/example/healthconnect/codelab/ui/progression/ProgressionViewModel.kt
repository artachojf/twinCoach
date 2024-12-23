package com.example.healthconnect.codelab.ui.progression

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGeneralInfoFeatures
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGeneralInfoFeatures
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGeneralInfoFeaturesParams
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressionViewModel @Inject constructor(
    private val getGeneralInfoFeatures: GetGeneralInfoFeatures,
    private val readGoogleId: ReadGoogleId,
    private val putGeneralInfoFeatures: PutGeneralInfoFeatures
) : ViewModel() {

    private var _features = MutableLiveData<DittoGeneralInfo.Features?>()
    val features get() = _features

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _error = MutableLiveData<DittoError>()
    val error get() = _error

    val putFeaturesSuccess = MutableLiveData<Boolean>()

    val putFeaturesError = MutableLiveData<DittoError?>()

    fun init() {
        getGoogleId()
    }

    fun getFeatures(googleId: String) {
        viewModelScope.launch {
            getGeneralInfoFeatures(googleId) {
                it.fold(::handleFeaturesError, ::handleFeaturesSuccess)
            }
        }
    }

    private fun handleFeaturesError(error: DittoError) {
        _error.postValue(error)
    }

    private fun handleFeaturesSuccess(info: DittoGeneralInfo.Features?) {
        _features.postValue(info)
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
                getFeatures(it)
                _googleId.postValue(it)
            }
        }
    }

    fun putGeneralInfoFeatures(
        googleId: String,
        modifiedFeatures: DittoGeneralInfo.Features
    ) {
        val params = PutGeneralInfoFeaturesParams(googleId, modifiedFeatures)
        viewModelScope.launch {
            putGeneralInfoFeatures(params) {
                it.fold(::handlePutFeaturesError, ::handlePutFeaturesSuccess)
            }
        }
    }

    private fun handlePutFeaturesError(error: DittoError) {
        putFeaturesError.postValue(error)
    }

    private fun handlePutFeaturesSuccess(unit: Unit) {
        putFeaturesSuccess.postValue(true)
        init()
    }
}