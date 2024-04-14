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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readUserInformation: ReadUserInformation,
    private val writeUserInformation: WriteUserInformation
) : ViewModel() {

    val noArrowFragments = listOf(R.id.homeFragment, R.id.loginFragment)

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
        val emptyUser = UserInformation("", "", "", null)
        viewModelScope.launch {
            writeUserInformation(emptyUser) {
                it.fold(::handleLogoutError, ::handleLogoutSuccess)
            }
        }
    }

    private fun handleLogoutError(unit: Unit) {
        _errorUserInformation.postValue("Error on logout")
    }

    private fun handleLogoutSuccess(unit: Unit) {
        val emptyUser = UserInformation("", "", "", null)
        _userInformation.postValue(emptyUser)
    }
}