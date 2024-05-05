package com.example.healthconnect.codelab.ui.home.adapter

import android.view.LayoutInflater
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
                    binding as ItemHomeListSuggestionsBinding
                )
                is HomeViewEntity.NextSessionEntity -> bindNextSession(
                    item.session,
                    binding as ItemHomeListTrainingBinding
                )
                is HomeViewEntity.GeneralInformationEntity -> bindGeneralInfo(
                    item.info,
                    binding as ItemHomeListInfoBinding
                )
                is HomeViewEntity.FatigueViewEntity -> bindFatigue(
                    item.fatigue,
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
            }
        }

        private fun bindSuggestions(
            suggestions: DittoGeneralInfo.Suggestions?,
            binding: ItemHomeListSuggestionsBinding
        ) {

        }

        private fun bindNextSession(
            session: DittoGeneralInfo.TrainingSession?,
            binding: ItemHomeListTrainingBinding
        ) {

        }

        private fun bindGeneralInfo(
            info: DittoGeneralInfo.Attributes?,
            binding: ItemHomeListInfoBinding) {

        }

        private fun bindFatigue(
            fatigue: Int?,
            binding: ItemHomeListFatigueBinding) {

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