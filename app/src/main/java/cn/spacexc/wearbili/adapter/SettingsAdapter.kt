package cn.spacexc.wearbili.adapter

import android.content.Context
import android.content.Intent
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
import cn.spacexc.wearbili.activity.settings.ChooseSettingsActivity
import cn.spacexc.wearbili.dataclass.settings.SettingItem
import cn.spacexc.wearbili.dataclass.settings.SettingType.*
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import cn.spacexc.wearbili.utils.ToastUtils

/**
 * Created by XC-Qan on 2022/8/18.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SettingsAdapter(private val context: Context) :
    ListAdapter<SettingItem, SettingsAdapter.SettingItemViewHolder>(object :
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
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ChooseSettingsActivity::class.java)
                    intent.putExtra("item", item)/*
                    intent.putExtra("itemKey", item.settingName)
                    intent.putExtra("itemName", item.displayName)
                    intent.putExtra("defVal", item.defString)*/
                    context.startActivity(intent)
                }
            }
            TYPE_SWITCH -> {
                holder.switch.isVisible = true
                holder.arrow.isVisible = false
                holder.switch.isChecked =
                    SharedPreferencesUtils.getBoolean(item.settingName, item.defBool)
                holder.switch.setOnCheckedChangeListener { _, b ->
                    SharedPreferencesUtils.saveBool(item.settingName, b)
                    ToastUtils.debugToast("${item.settingName}: $b")
                }
                holder.itemView.setOnClickListener {
                    holder.switch.isChecked = !holder.switch.isChecked
                }
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