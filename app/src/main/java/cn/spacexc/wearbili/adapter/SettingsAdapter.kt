package cn.spacexc.wearbili.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.settings.SettingItem
import cn.spacexc.wearbili.dataclass.settings.SettingType.*

/**
 * Created by XC-Qan on 2022/8/18.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SettingsAdapter : ListAdapter<SettingItem, SettingsAdapter.SettingItemViewHolder>(object :
    DiffUtil.ItemCallback<SettingItem>() {
    override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem.settingName == newItem.settingName
    }

    override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem.displayName == newItem.displayName
    }

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingItemViewHolder {
        return SettingItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_setting_item_button, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.displayName
        holder.description.text = item.description
        holder.icon.setImageResource(item.iconRes)
        holder.name.setTextColor(Color.parseColor(item.color))
        holder.icon.setColorFilter(Color.parseColor(item.color))
        holder.description.setTextColor(Color.parseColor(item.color))
        when (item.type) {
            TYPE_ACTION -> {
                holder.switch.isVisible = false
                holder.arrow.isVisible = false
                holder.itemView.setOnClickListener {
                    item.action.invoke()
                }
            }
            TYPE_CHOOSE -> {
                holder.switch.isVisible = false
                holder.arrow.isVisible = true
                //TODO(Not yet implemented)
            }
            TYPE_SWITCH -> {
                holder.switch.isVisible = true
                holder.arrow.isVisible = false
                //TODO(Not yet implemented)
            }
            TYPE_CATEGORY -> {
                holder.switch.isVisible = false
                holder.arrow.isVisible = true
                //TODO(Not yet implemented)
            }
        }
    }

    class SettingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView
        val description: TextView
        val switch: Switch
        val arrow: ImageView

        init {
            icon = itemView.findViewById(R.id.uploaderAvatar)
            name = itemView.findViewById(R.id.usernameText)
            description = itemView.findViewById(R.id.survey)
            switch = itemView.findViewById(R.id.switch1)
            arrow = itemView.findViewById(R.id.arrow)
        }
    }
}