package com.example.healthconnect.codelab.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.healthconnect.codelab.databinding.ItemHomeListFatigueBinding
import com.example.healthconnect.codelab.databinding.ItemHomeListGoalBinding
import com.example.healthconnect.codelab.databinding.ItemHomeListInfoBinding
import com.example.healthconnect.codelab.databinding.ItemHomeListSuggestionsBinding
import com.example.healthconnect.codelab.databinding.ItemHomeListTrainingBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.ui.home.HomeViewEntity
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toHeightString
import com.example.healthconnect.codelab.utils.toTimeString
import com.example.healthconnect.codelab.utils.toWeigthString

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var list: List<HomeViewEntity> = emptyList()

    class ViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeViewEntity) {
            when (item) {
                is HomeViewEntity.GoalViewEntity -> bindGoal(
                    item.goal,
                    item.callback,
                    binding as ItemHomeListGoalBinding
                )
                is HomeViewEntity.SuggestionsViewEntity -> bindSuggestions(
                    item.suggestions,
                    item.callback,
                    binding as ItemHomeListSuggestionsBinding
                )
                is HomeViewEntity.NextSessionEntity -> bindNextSession(
                    item.session,
                    item.callback,
                    binding as ItemHomeListTrainingBinding
                )
                is HomeViewEntity.GeneralInformationEntity -> bindGeneralInfo(
                    item.info,
                    item.callback,
                    binding as ItemHomeListInfoBinding
                )
                is HomeViewEntity.FatigueViewEntity -> bindFatigue(
                    item.fatigue,
                    item.callback,
                    binding as ItemHomeListFatigueBinding
                )
            }
        }

        private fun bindGoal(
            goal: DittoGeneralInfo.Goal?,
            callback: () -> Unit,
            binding: ItemHomeListGoalBinding
        ) {

            binding.apply {
                root.setOnClickListener {
                    callback.invoke()
                }

                if (goal != null) {
                    tvEmptyState.visibility = View.GONE

                    tvTime.text = goal.seconds.toTimeString()
                    tvDistance.text = goal.distance.toDistanceString()
                    tvEstimation.text = goal.estimations.lastOrNull()?.seconds?.toTimeString() ?: "-"
                    tvDate.text = goal.date.toString()

                    constraint.visibility = View.VISIBLE
                }
            }
        }

        private fun bindSuggestions(
            suggestions: DittoGeneralInfo.Suggestions?,
            callback: () -> Unit,
            binding: ItemHomeListSuggestionsBinding
        ) {

            binding.apply {
                root.setOnClickListener {
                    callback.invoke()
                }

                if (suggestions != null) {
                    tvSuggestionsNumber.text = suggestions.suggestions.size.toString()
                }
            }
        }

        private fun bindNextSession(
            session: DittoGeneralInfo.TrainingSession?,
            callback: () -> Unit,
            binding: ItemHomeListTrainingBinding
        ) {

            binding.apply {
                root.setOnClickListener {
                    callback.invoke()
                }

                if (session != null) {
                    tvEmptyState.visibility = View.GONE

                    tvNextSession.text = session.day.toString()
                    tvDistance.text = "${session.times} x ${session.distance.toDistanceString()}"
                    tvTime.text = session.expectedTime.toTimeString()
                    tvHeart.text = "${session.meanHeartRate} bpm"
                    tvRest.text = session.rest.toTimeString()

                    constraint.visibility = View.VISIBLE
                }
            }
        }

        private fun bindGeneralInfo(
            info: DittoGeneralInfo.Attributes?,
            callback: () -> Unit,
            binding: ItemHomeListInfoBinding
        ) {

            binding.apply {
                root.setOnClickListener {
                    callback.invoke()
                }

                if (info != null) {
                    tvEmptyState.visibility = View.GONE

                    tvHeight.text = info.height.toHeightString()
                    tvWeight.text = info.weight.toWeigthString()
                    tvBirthdate.text = info.birthdate.toString()
                    tvRunningDate.text = info.runningDate.toString()

                    constraint.visibility = View.VISIBLE
                }
            }
        }

        private fun bindFatigue(
            fatigue: Int?,
            callback: () -> Unit,
            binding: ItemHomeListFatigueBinding
        ) {

            binding.apply {
                root.setOnClickListener {
                    callback.invoke()
                }

                if (fatigue != null) {
                    tvEmptyState.visibility = View.GONE
                    constraint.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                ViewHolder(
                    ItemHomeListGoalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            1 -> {
                ViewHolder(
                    ItemHomeListSuggestionsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            2 -> {
                ViewHolder(
                    ItemHomeListTrainingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            3 -> {
                ViewHolder(
                    ItemHomeListInfoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            4 -> {
                ViewHolder(
                    ItemHomeListFatigueBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                ViewHolder(
                    ItemHomeListGoalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is HomeViewEntity.GoalViewEntity -> 0
            is HomeViewEntity.SuggestionsViewEntity -> 1
            is HomeViewEntity.NextSessionEntity -> 2
            is HomeViewEntity.GeneralInformationEntity -> 3
            is HomeViewEntity.FatigueViewEntity -> 4
        }
    }

    fun submitList(list: List<HomeViewEntity>) {
        this.list = list
        notifyDataSetChanged()
    }
}