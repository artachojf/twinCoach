package com.example.healthconnect.codelab.domain.model.userInformation

import android.net.Uri
import com.example.healthconnect.codelab.data.model.userInformation.UserInformationModel

data class UserInformation(
    val googleId: String,
    val name: String,
    val email: String,
    val profilePicture: Uri?
)

fun UserInformationModel.toDomain(): UserInformation =
    UserInformation(
        googleId,
        name,
        email,
        Uri.parse(profilePicture)
    )