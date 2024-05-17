package com.example.healthconnect.codelab.ui.sessions.list

import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import java.time.LocalDateTime

sealed class SessionsListViewEntity {

    data class Label(val label: String) : SessionsListViewEntity()
    data class SuggestedSession(val session: DittoGeneralInfo.TrainingSession) : SessionsListViewEntity()
    data class CompletedSession(val session: DittoCurrentState.Thing) : SessionsListViewEntity()
}