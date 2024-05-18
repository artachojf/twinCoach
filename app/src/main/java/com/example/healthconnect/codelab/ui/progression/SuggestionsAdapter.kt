package com.example.healthconnect.codelab.ui.progression

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthconnect.codelab.databinding.ItemSuggestionsListBinding

class SuggestionsAdapter(

) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    private var list: List<SuggestionViewEntity> = emptyList()

    class ViewHolder(
        private val binding: ItemSuggestionsListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestionViewEntity) = binding.apply {
            root.setOnClickListener { item.callback() }
            tvSuggestion.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSuggestionsListBinding.inflate(
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

    fun submitList(list: List<SuggestionViewEntity>) {
        this.list = list
        notifyDataSetChanged()
    }
}