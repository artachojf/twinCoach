package com.example.healthconnect.codelab.ui.sessions.info.completed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthconnect.codelab.databinding.ItemLapsListBinding
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString

class LapsListAdapter(
    private val list: List<LapsViewEntity>
) : RecyclerView.Adapter<LapsListAdapter.ViewHolder>() {
    class ViewHolder(
        val binding: ItemLapsListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LapsViewEntity) = binding.apply {
            when (item) {
                is LapsViewEntity.Header -> {
                    tvColumn1.text = item.column1
                    tvColumn2.text = item.column2
                    tvColumn3.text = item.column3
                }

                is LapsViewEntity.Item -> {
                    tvColumn1.text = item.distance.toDistanceString()
                    tvColumn2.text = item.time.toTimeString()
                    tvColumn3.text = item.pace.toSpeedString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLapsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}