package com.example.healthconnect.codelab.domain.model.userInformation

import android.net.Uri
import com.example.healthconnect.codelab.data.model.userInformation.UserInformationModel
import java.time.LocalDate

data class UserInformation(
    val googleId: String,
    val name: String,
    val email: String,
    val profilePicture: Uri?,
    val birthdate: LocalDate?
)

fun UserInformationModel.toDomain(): UserInformation =
    UserInformation(
        googleId,
        name,
        email,
        Uri.parse(profilePicture),
        if (birthdate.isNullOrEmpty()) null else LocalDate.parse(birthdate)
    )