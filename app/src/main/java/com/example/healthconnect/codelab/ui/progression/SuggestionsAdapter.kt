package com.example.healthconnect.codelab.ui.progression

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthconnect.codelab.databinding.ItemSuggestionsListBinding
import com.example.healthconnect.codelab.ui.progression.model.SuggestionViewEntity

class SuggestionsAdapter(
    private val onSuggestionClick: (SuggestionViewEntity) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    private var list: List<SuggestionViewEntity> = emptyList()

    class ViewHolder(
        private val binding: ItemSuggestionsListBinding,
        private val onSuggestionClick: (SuggestionViewEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuggestionViewEntity) = binding.apply {
            root.setOnClickListener { onSuggestionClick(item) }
            tvSuggestion.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSuggestionsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onSuggestionClick
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