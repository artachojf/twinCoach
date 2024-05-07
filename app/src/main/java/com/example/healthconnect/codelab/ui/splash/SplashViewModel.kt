package com.example.healthconnect.codelab.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadUserInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val readUserInformation: ReadUserInformation
) : ViewModel() {

    private val _userInformation = MutableLiveData<UserInformation>()
    val userInformation get() = _userInformation

    private val _errorUserInformation = MutableLiveData<String>()
    val errorUserInformation get() = _errorUserInformation

    fun readUserInformation() {
        readUserInformation(Unit) {
            it.fold(::handleUserInfoError, ::handleUserInformationSuccess)
        }
    }

    private fun handleUserInfoError(unit: Unit) {
        _errorUserInformation.postValue("Error loading user information")
    }

    private fun handleUserInformationSuccess(flow: Flow<UserInformation>) {
        viewModelScope.launch {
            flow.collect {
                _userInformation.postValue(it)
            }
        }
    }
}