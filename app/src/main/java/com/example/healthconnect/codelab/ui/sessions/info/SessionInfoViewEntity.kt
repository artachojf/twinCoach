package com.example.healthconnect.codelab.ui.sessions.info

import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class SessionInfoViewEntity : java.io.Serializable {

    data class Session(
        val session: DittoCurrentState.TrainingSession?,
        val date: LocalDateTime
    ): SessionInfoViewEntity()
    data class Suggestion(val suggestion: DittoGeneralInfo.TrainingSession?): SessionInfoViewEntity()
}