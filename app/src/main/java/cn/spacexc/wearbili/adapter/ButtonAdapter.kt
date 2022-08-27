package cn.spacexc.wearbili.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale

/**
 * Created by XC-Qan on 2022/6/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ButtonsAdapter(isOutlined: Boolean, onItemViewClickListener: OnItemViewClickListener) :
    ListAdapter<RoundButtonData, ButtonsAdapter.ButtonViewHolder>(object :
        DiffUtil.ItemCallback<RoundButtonData>() {
        override fun areItemsTheSame(oldItem: RoundButtonData, newItem: RoundButtonData): Boolean {
            return oldItem.displayName == newItem.displayName
        }

        override fun areContentsTheSame(
            oldItem: RoundButtonData,
            newItem: RoundButtonData
        ): Boolean {
            return oldItem.displayName == newItem.displayName
        }
    }) {

    private var outline: Boolean = false
    private var onItemViewClickListener: OnItemViewClickListener
    init {
        outline = isOutlined
        this.onItemViewClickListener = onItemViewClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        if (outline) {
            return ButtonViewHolder(
                inflater.inflate(
                    R.layout.cell_round_button_otld,
                    parent,
                    false
                )
            )
        } else {
            return ButtonViewHolder(
                inflater.inflate(
                    R.layout.cell_round_button,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.addClickScale()
        holder.icon.setImageResource(item.resId)
        holder.name.text = item.displayName
        println(item.displayName)
        holder.itemView.setOnClickListener {
            onItemViewClickListener.onClick(item.buttonName, holder)
        }
        holder.itemView.setOnLongClickListener {
            onItemViewClickListener.onLongClick(item.buttonName, holder)
            true
        }
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView

        init {
            icon = itemView.findViewById(R.id.uploaderAvatar)
            name = itemView.findViewById(R.id.buttonName)
        }
    }


}