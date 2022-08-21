package cn.spacexc.wearbili.listener

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by XC-Qan on 2022/7/6.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

interface OnItemViewClickListener {
    fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder)
    fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder)
}