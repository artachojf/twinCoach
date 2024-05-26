package com.example.healthconnect.codelab.ui.sessions.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.healthconnect.codelab.databinding.ItemListLabelBinding
import com.example.healthconnect.codelab.databinding.ItemSessionsListBinding
import com.example.healthconnect.codelab.ui.sessions.list.SessionsListViewEntity
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toFormatString
import com.example.healthconnect.codelab.utils.toHeartRateString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString

class SessionsListAdapter(
    private val onItemClick: (SessionsListViewEntity) -> Unit
) : RecyclerView.Adapter<SessionsListAdapter.ViewHolder>() {

    private var list: List<SessionsListViewEntity> = emptyList()

    class ViewHolder(
        private val binding: ViewBinding,
        private val onItemClick: (SessionsListViewEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SessionsListViewEntity) {
            when (item) {
                is SessionsListViewEntity.Label -> bindLabel(item, binding as ItemListLabelBinding)

                is SessionsListViewEntity.CompletedSession ->
                    bindCompletedSession(item, binding as ItemSessionsListBinding)

                is SessionsListViewEntity.SuggestedSession ->
                    bindSuggestedSession(item, binding as ItemSessionsListBinding)
            }
        }

        private fun bindLabel(
            item: SessionsListViewEntity.Label,
            binding: ItemListLabelBinding
        ) {
            binding.tvLabel.text = item.label
        }

        private fun bindSuggestedSession(
            item: SessionsListViewEntity.SuggestedSession,
            binding: ItemSessionsListBinding
        ) {
            binding.apply {
                tvNextSession.text = item.session.day.toString()
                tvDistance.text =
                    if (item.session.times > 1) "${item.session.times} x ${item.session.distance.toDistanceString()}"
                    else item.session.distance.toDistanceString()
                tvTime.text = item.session.expectedTime.toTimeString()
                tvHeart.text = item.session.meanHeartRate.toHeartRateString()
                tvRest.text = item.session.rest.toTimeString()

                ivRest.visibility = View.VISIBLE
                tvRest.visibility = View.VISIBLE
                ivPace.visibility = View.GONE
                tvPace.visibility = View.GONE

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        private fun bindCompletedSession(
            item: SessionsListViewEntity.CompletedSession,
            binding: ItemSessionsListBinding
        ) {
            binding.apply {
                val distance = item.session.features.trainingSession.getTotalDistance()
                val time = item.session.features.trainingSession.getTotalTime()

                tvNextSession.text = item.session.attributes.date.toFormatString()
                tvDistance.text = distance.toInt().toDistanceString()
                tvTime.text = time.toInt().toTimeString()
                tvHeart.text
                tvPace.text = ((time/60) / (distance / 1000)).toSpeedString()

                ivPace.visibility = View.VISIBLE
                tvPace.visibility = View.VISIBLE
                ivRest.visibility = View.GONE
                tvRest.visibility = View.GONE

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                ViewHolder(
                    ItemListLabelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClick
                )
            }

            else -> {
                ViewHolder(
                    ItemSessionsListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClick
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int =
        list.size

    fun submitList(list: List<SessionsListViewEntity>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is SessionsListViewEntity.Label -> 0
            is SessionsListViewEntity.SuggestedSession -> 1
            is SessionsListViewEntity.CompletedSession -> 2
        }
    }
}