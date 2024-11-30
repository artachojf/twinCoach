package com.example.healthconnect.codelab.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation
import com.example.healthconnect.codelab.domain.usecase.userInformation.WriteUserInformation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val writeUserInformation: WriteUserInformation
) : ViewModel() {

    private val _userInformation = MutableLiveData<UserInformation>()
    val userInformation get() = _userInformation

    fun onLogin(account: GoogleSignInAccount) {
        val user = UserInformation(
            account.id.orEmpty(),
            account.displayName.orEmpty(),
            account.email.orEmpty(),
            account.photoUrl,
            LocalDate.now()
        )
        viewModelScope.launch {
            writeUserInformation(user) {}
        }
    }
}