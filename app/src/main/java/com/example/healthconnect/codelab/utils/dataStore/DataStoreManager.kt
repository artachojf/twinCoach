package com.example.healthconnect.codelab.utils.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.healthconnect.codelab.model.userInformation.UserInformation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun readString(key: String): Flow<String> {
        return dataStore.data.map {
            it[stringPreferencesKey(key)].orEmpty()
        }
    }

    suspend fun writeString(key: String, value: String) {
        dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    fun readUserInformation(): Flow<UserInformation> {
        return dataStore.data.map {
            UserInformation(
                googleId = it[stringPreferencesKey("googleId")].orEmpty(),
                name = it[stringPreferencesKey("name")].orEmpty(),
                email = it[stringPreferencesKey("email")].orEmpty(),
                profilePicture = it[stringPreferencesKey("profilePicture")].orEmpty()
            )
        }
    }

    suspend fun writeUserInformation(account: GoogleSignInAccount) {
        writeString("googleId", account.id.orEmpty())
        writeString("email", account.email.orEmpty())
        writeString("name", account.displayName.orEmpty())
        writeString("profilePicture", account.photoUrl.toString())
    }
}