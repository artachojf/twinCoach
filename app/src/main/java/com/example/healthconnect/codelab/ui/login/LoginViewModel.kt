package com.example.healthconnect.codelab.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.utils.DataStoreManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    fun onLogin(account: GoogleSignInAccount) {
        viewModelScope.launch {
            dataStoreManager.writeUserInformation(account)
        }
    }

    fun readGoogleId(): Flow<String> {
        return dataStoreManager.readString("googleId")
    }
}