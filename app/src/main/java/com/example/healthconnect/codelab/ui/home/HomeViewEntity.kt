package com.example.healthconnect.codelab.ui.home

import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import java.time.LocalDate

sealed class HomeViewEntity(
) {

    data class GoalViewEntity(
        val goal: DittoGeneralInfo.Goal? = null,
        val callback: () -> Unit
    ) : HomeViewEntity()

    data class SuggestionsViewEntity(
        val suggestions: DittoGeneralInfo.Suggestions? = null,
        val callback: () -> Unit
    ) : HomeViewEntity()

    data class NextSessionEntity(
        val session: DittoGeneralInfo.TrainingSession? = null,
        val callback: () -> Unit
    ) : HomeViewEntity()

    data class GeneralInformationEntity(
        val info: DittoGeneralInfo.Attributes? = null,
        val callback: () -> Unit
    ) : HomeViewEntity()

    data class FatigueViewEntity(
        val fatigue: Int? = null,
        val callback: () -> Unit
    ) : HomeViewEntity()
}