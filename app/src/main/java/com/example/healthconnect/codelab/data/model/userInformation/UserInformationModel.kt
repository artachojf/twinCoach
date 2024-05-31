package com.example.healthconnect.codelab.data.model.userInformation

import com.example.healthconnect.codelab.domain.model.userInformation.UserInformation

data class UserInformationModel(
    val googleId: String,
    val name: String,
    val email: String,
    val profilePicture: String,
    val birthdate: String?
)

fun UserInformation.toData(): UserInformationModel =
    UserInformationModel(
        googleId,
        name,
        email,
        profilePicture.toString(),
        birthdate.toString()
    )