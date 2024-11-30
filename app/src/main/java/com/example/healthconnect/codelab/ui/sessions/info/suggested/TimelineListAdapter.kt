package com.example.healthconnect.codelab.ui.sessions.info.suggested

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthconnect.codelab.databinding.ItemTimelineListBinding

class TimelineListAdapter(
    private val list: List<TimelineViewEntity>
) : RecyclerView.Adapter<TimelineListAdapter.ViewHolder>() {
    class ViewHolder(
        private val binding: ItemTimelineListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TimelineViewEntity, first: Boolean, last: Boolean) = binding.apply {
            if (first) upperSection.visibility = View.GONE
            else if (last) lowerSection.visibility = View.GONE

            tvTitle.text = item.title
            tvDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTimelineListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position==0, position==list.size-1)
    }
}