package com.example.healthconnect.codelab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.utils.dataStore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val noArrowFragments = listOf(R.id.homeFragment, R.id.loginFragment)

    fun onLogout() {
        viewModelScope.launch {
            dataStoreManager.writeString("googleId", "")
        }
    }

    /*
    TODO: This will go on settings
    private fun logout() {
        signInClient.signOut().addOnCompleteListener {
            mainViewModel.onLogout()
            navigate to login page
        }
    }
    */
}