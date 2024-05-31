package com.example.healthconnect.codelab.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.userInformation.ReadUserInformation
import com.example.healthconnect.codelab.domain.usecase.userInformation.WriteUserInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readUserInformation: ReadUserInformation,
    private val writeUserInformation: WriteUserInformation
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

    fun onLogout() {
        viewModelScope.launch {
            writeUserInformation(EMPTY_USER) {
                it.fold(::handleLogoutError, ::handleLogoutSuccess)
            }
        }
    }

    private fun handleLogoutError(unit: Unit) {
        _errorUserInformation.postValue("Error on logout")
    }

    private fun handleLogoutSuccess(unit: Unit) {
        _userInformation.postValue(EMPTY_USER)
    }

    private companion object {
        val EMPTY_USER = UserInformation("", "", "", null, LocalDate.now())
    }
}