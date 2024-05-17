package com.example.healthconnect.codelab.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGeneralInfoThing
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val retrieveGeneralInfo: GetGeneralInfoThing,
    private val readGoogleId: ReadGoogleId
) : ViewModel() {

    private var _generalInfo = MutableLiveData<DittoGeneralInfo.Thing?>()
    val generalInfo get() = _generalInfo

    private var _error = MutableLiveData<DittoError>()
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

    private fun handleGeneralInfoError(error: DittoError) {
        _error.postValue(error)
    }

    private fun handleGeneralInfoSuccess(info: DittoGeneralInfo.Thing?) {
        _generalInfo.postValue(info)
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
                getGeneralInfo(it)
            }
        }
    }
}