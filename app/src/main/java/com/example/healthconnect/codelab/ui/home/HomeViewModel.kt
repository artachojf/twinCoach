package com.example.healthconnect.codelab.ui.home

import androidx.lifecycle.ViewModel
import com.example.healthconnect.codelab.utils.dataStore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val googleId = dataStoreManager.readUserInformation()
}