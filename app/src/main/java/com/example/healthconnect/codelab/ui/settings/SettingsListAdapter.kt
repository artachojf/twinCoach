package com.example.healthconnect.codelab.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.ui.settings.model.SettingsViewEntity

class SettingsListAdapter(
    private val settingsList: List<SettingsViewEntity>
): RecyclerView.Adapter<SettingsListAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivSettingOption)
        val text: TextView = view.findViewById(R.id.tvSettingOption)

        fun bind(item: SettingsViewEntity) {
            image.visibility = View.GONE
            when(item) {
                is SettingsViewEntity.DangerousSetting -> {
                    text.text = item.text
                    itemView.setOnClickListener {
                        item.action.invoke()
                    }
                }

                is SettingsViewEntity.RegularSetting -> {
                    text.text = item.text
                    itemView.setOnClickListener {
                        item.action.invoke()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val viewLayout = when(viewType) {
            0 -> LayoutInflater.from(parent.context)
                .inflate(R.layout.regular_settings_list_item, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.dangerous_settings_list_item, parent, false)
        }
        return SettingsViewHolder(viewLayout)
    }

    override fun getItemCount(): Int = settingsList.size

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = settingsList[position]
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return when(settingsList[position]) {
            is SettingsViewEntity.RegularSetting -> 0
            is SettingsViewEntity.DangerousSetting -> 1
        }
    }
}