package com.example.healthconnect.codelab

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.codelab.utils.DataStoreManager
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