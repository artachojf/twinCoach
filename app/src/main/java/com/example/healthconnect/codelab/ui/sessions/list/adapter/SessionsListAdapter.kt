package com.example.healthconnect.codelab.ui.sessions.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.healthconnect.codelab.databinding.ItemListLabelBinding
import com.example.healthconnect.codelab.databinding.ItemSessionsListBinding
import com.example.healthconnect.codelab.ui.sessions.info.SessionInfoViewEntity
import com.example.healthconnect.codelab.ui.sessions.list.SessionsListViewEntity

class SessionsListAdapter(
    private val onItemClick: (SessionInfoViewEntity) -> Unit
) : RecyclerView.Adapter<SessionsListAdapter.ViewHolder>() {

    private var list: List<SessionsListViewEntity> = emptyList()

    class ViewHolder(
        private val binding: ViewBinding,
        private val onItemClick: (SessionInfoViewEntity) -> Unit
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
                tvDistance.text = item.session.times.toString() + " X " + item.session.distance.toString()
                tvTime.text = item.session.expectedTime.toString()
                tvHeart.text = ""
                tvRest.text = item.session.rest.toString()

                root.setOnClickListener {
                    onItemClick(
                        SessionInfoViewEntity.Suggestion(item.session)
                    )
                }
            }
        }

        private fun bindCompletedSession(
            item: SessionsListViewEntity.CompletedSession,
            binding: ItemSessionsListBinding
        ) {
            binding.apply {
                tvNextSession.text = item.date.toString()

                root.setOnClickListener {
                    onItemClick(
                        SessionInfoViewEntity.Session(item.session, item.date)
                    )
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