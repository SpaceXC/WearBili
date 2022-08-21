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
import cn.spacexc.wearbili.dataclass.HorizontalButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener

/**
 * Created by XC-Qan on 2022/6/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class HorizontalButtonAdapter(val onItemViewClickListener: OnItemViewClickListener) :
    ListAdapter<HorizontalButtonData, HorizontalButtonAdapter.ButtonViewHolder>(object :
        DiffUtil.ItemCallback<HorizontalButtonData>() {
        override fun areItemsTheSame(
            oldItem: HorizontalButtonData,
            newItem: HorizontalButtonData
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: HorizontalButtonData,
            newItem: HorizontalButtonData
        ): Boolean {
            return false
        }

    }) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)

        return ButtonViewHolder(
            inflater.inflate(
                R.layout.cell_card_horizontal_button,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.icon.setImageResource(getItem(position).iconResId)
        holder.name.text = getItem(position).mainText
        holder.description.text = getItem(position).description
        holder.itemView.setOnClickListener {
            onItemViewClickListener.onClick(getItem(position).mainText, holder)
        }
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView
        val description: TextView

        init {
            icon = itemView.findViewById(R.id.uploaderAvatar)
            name = itemView.findViewById(R.id.usernameText)
            description = itemView.findViewById(R.id.survey)
        }
    }
}