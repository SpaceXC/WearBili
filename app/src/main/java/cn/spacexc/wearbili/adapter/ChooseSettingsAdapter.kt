package cn.spacexc.wearbili.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.settings.RequireRestartActivity
import cn.spacexc.wearbili.dataclass.settings.ChooseItem
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale

/**
 * Created by XC-Qan on 2022/8/21.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ChooseSettingsAdapter(
    private val key: String,
    private val defVal: String,
    private val requiresRestart: Boolean = false,
    private val context: Context
) :
    ListAdapter<ChooseItem, ChooseSettingsAdapter.ChooseSettingItemViewHolder>(object :
        DiffUtil.ItemCallback<ChooseItem>() {
        override fun areItemsTheSame(oldItem: ChooseItem, newItem: ChooseItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ChooseItem, newItem: ChooseItem): Boolean {
            return oldItem.displayName == newItem.displayName
        }

    }) {
    var prevSelectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseSettingItemViewHolder {
        return ChooseSettingItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_radio_setting_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChooseSettingItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.radioButton.isChecked = item.name == SharedPreferencesUtils.getString(key, defVal)
        if (item.name == SharedPreferencesUtils.getString(key, defVal)) {
            prevSelectedItem = position
        }
        holder.name.text = item.displayName
        holder.icon.setImageResource(item.icon)
        holder.description.text = item.description
        holder.radioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                SharedPreferencesUtils.saveString(key, item.name)
                ToastUtils.debugToast("${key}: ${item.name}")
                notifyItemChanged(prevSelectedItem)
                prevSelectedItem = position
            }
            if (requiresRestart) {
                Intent(context, RequireRestartActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
        }
        holder.itemView.setOnClickListener {
            holder.radioButton.isChecked = true
        }
        holder.itemView.addClickScale()
    }

    class ChooseSettingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView
        val description: TextView
        val radioButton: RadioButton

        init {
            icon = itemView.findViewById(R.id.icon)
            name = itemView.findViewById(R.id.mainText)
            description = itemView.findViewById(R.id.description)
            radioButton = itemView.findViewById(R.id.radioButton)

        }
    }
}