package com.example.healthconnect.codelab.ui.progression

import android.content.Context
import androidx.navigation.NavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toTimeString

data class SuggestionViewEntity(
    val description: String,
    val callback: () -> Unit
)

fun DittoGeneralInfo.Suggestion.toPresentation(context: Context, navController: NavController): SuggestionViewEntity {
    return when (this) {
        is DittoGeneralInfo.Suggestion.SmallerGoal -> {
            SuggestionViewEntity(
                "${context.getString(R.string.smaller_goal_1)} ${this.seconds.toTimeString()} ${context.getString(R.string.smaller_goal_2)} ${this.distance.toDistanceString()}?"
            ) {

            }
        }

        is DittoGeneralInfo.Suggestion.BiggerGoal -> {
            SuggestionViewEntity(
                "${context.getString(R.string.bigger_goal_1)} ${this.seconds.toTimeString()} ${context.getString(R.string.bigger_goal_2)} ${this.distance.toDistanceString()}?"
            ) {

            }
        }

        is DittoGeneralInfo.Suggestion.LessTrainingDays -> {
            SuggestionViewEntity(
                "${context.getString(R.string.less_training_days_1)} ${this.trainingDays.size} ${context.getString(R.string.less_training_days_2)}"
            ) {

            }
        }

        is DittoGeneralInfo.Suggestion.MoreTrainingDays -> {
            SuggestionViewEntity(
                "${context.getString(R.string.more_training_days_1)} ${this.trainingDays.size} ${context.getString(R.string.more_training_days_2)}"
            ) {

            }
        }
    }
}