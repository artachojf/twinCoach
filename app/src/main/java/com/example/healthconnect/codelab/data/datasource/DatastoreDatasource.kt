package com.example.healthconnect.codelab.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import arrow.core.Either
import com.example.healthconnect.codelab.data.model.userInformation.UserInformationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatastoreDatasource @Inject constructor(
    private val datastore: DataStore<Preferences>
) {

    fun readString(key: String): Either<Unit, Flow<String>> {
        return try {
            Either.Right(datastore.data.map {
                it[stringPreferencesKey(key)].orEmpty()
            })
        } catch (_: Exception) {
            Either.Left(Unit)
        }
    }

    suspend fun writeString(key: String, value: String): Either<Unit, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                datastore.edit {
                    it[stringPreferencesKey(key)] = value
                }
                Either.Right(Unit)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }

    fun readUserInformation(): Either<Unit, Flow<UserInformationModel>> {
        return try {
            Either.Right(datastore.data.map {
                UserInformationModel(
                    googleId = it[stringPreferencesKey("googleId")].orEmpty(),
                    name = it[stringPreferencesKey("name")].orEmpty(),
                    email = it[stringPreferencesKey("email")].orEmpty(),
                    profilePicture = it[stringPreferencesKey("profilePicture")].orEmpty()
                )
            })
        } catch (_: Exception) {
            Either.Left(Unit)
        }
    }

    suspend fun writeUserInformation(user: UserInformationModel): Either<Unit, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                datastore.edit {
                    it[stringPreferencesKey("googleId")] = user.googleId
                    it[stringPreferencesKey("email")] = user.email
                    it[stringPreferencesKey("name")] = user.name
                    it[stringPreferencesKey("profilePicture")] = user.profilePicture
                }
                Either.Right(Unit)
            } catch (_: Exception) {
                Either.Left(Unit)
            }
        }
    }
}