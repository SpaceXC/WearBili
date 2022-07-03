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
import cn.spacexc.wearbili.dataclass.ButtonData

/**
 * Created by XC-Qan on 2022/6/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ButtonsAdapter(isOutlined: Boolean) :
    ListAdapter<ButtonData, ButtonsAdapter.ButtonViewHolder>(object :
        DiffUtil.ItemCallback<ButtonData>() {
        override fun areItemsTheSame(oldItem: ButtonData, newItem: ButtonData): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: ButtonData, newItem: ButtonData): Boolean {
            return false
        }
    }) {

    private var outline: Boolean = false

    init {
        outline = isOutlined
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
        holder.icon.setImageResource(getItem(position).resId)
        holder.name.text = getItem(position).buttonName
        holder.itemView.setOnClickListener {
            getItem(position).block.invoke()
        }
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView

        init {
            icon = itemView.findViewById(R.id.icon)
            name = itemView.findViewById(R.id.buttonName)
        }
    }
}