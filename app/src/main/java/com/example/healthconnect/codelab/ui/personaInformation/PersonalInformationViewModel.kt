package com.example.healthconnect.codelab.ui.personaInformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.data.model.failure.ResponseFailure
import com.example.healthconnect.codelab.domain.model.ditto.DittoError
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.domain.usecase.ditto.get.generalInfo.GetGeneralInfoAttributes
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGeneralInfoAttributes
import com.example.healthconnect.codelab.domain.usecase.ditto.put.generalInfo.PutGeneralInfoAttributesParams
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadGoogleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PersonalInformationViewModel @Inject constructor(
    private val readGoogleId: ReadGoogleId,
    private val getGeneralInfoAttributes: GetGeneralInfoAttributes,
    private val putGeneralInfoAttributes: PutGeneralInfoAttributes
) : ViewModel() {

    private var _personalInformation = MutableLiveData<DittoGeneralInfo.Attributes?>()
    val personalInformation get() = _personalInformation

    private var _googleId = MutableLiveData<String>()
    val googleId get() = _googleId

    private var _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess get() = _updateSuccess

    private var _error = MutableLiveData<ResponseFailure>()
    val error get() = _error

    var birthdate: LocalDate? = null
    var runningDate: LocalDate? = null

    fun init() {
        readGoogleId()
    }

    fun readGoogleId() {
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
                getPersonalInformation(it)
                _googleId.postValue(it)
            }
        }
    }

    private fun getPersonalInformation(googleId: String) {
        viewModelScope.launch {
            getGeneralInfoAttributes(googleId) {
                it.fold(::handlePersonalInformationFailure, ::handlePersonalInformationSuccess)
            }
        }
    }

    private fun handlePersonalInformationFailure(error: DittoError) {
        _error.postValue(error.failure)
    }

    private fun handlePersonalInformationSuccess(attributes: DittoGeneralInfo.Attributes?) {
        _personalInformation.postValue(attributes)
    }

    fun updatePersonalInformation(googleId: String, attributes: DittoGeneralInfo.Attributes) {
        viewModelScope.launch {
            putGeneralInfoAttributes(
                PutGeneralInfoAttributesParams(googleId, attributes)
            ) {
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