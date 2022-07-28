package cn.spacexc.wearbili.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.SearchResultActivity
import cn.spacexc.wearbili.dataclass.HotSearchData
import com.bumptech.glide.Glide

/**
 * Created by XC-Qan on 2022/6/29.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class HotSearchAdapter : ListAdapter<HotSearchData, HotSearchAdapter.HotSearchViewHolder>(object :
    DiffUtil.ItemCallback<HotSearchData>() {
    override fun areItemsTheSame(oldItem: HotSearchData, newItem: HotSearchData): Boolean {
        return oldItem.hot_id == newItem.hot_id
    }

    override fun areContentsTheSame(oldItem: HotSearchData, newItem: HotSearchData): Boolean {
        return oldItem.show_name == newItem.show_name
    }

}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotSearchViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return HotSearchViewHolder(
            inflater.inflate(
                R.layout.cell_hot_search,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HotSearchViewHolder, position: Int) {
        val searchData = getItem(position)
        holder.showNameText.text = searchData.show_name
        holder.itemView.setOnClickListener {
            val intent = Intent(Application.getContext(), SearchResultActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("keyword", searchData.keyword)
            Application.getContext().startActivity(intent)
        }
        if (searchData.word_type == 1) {
            holder.hotTypeImageView.visibility = View.GONE
        } else {
            Glide.with(Application.getContext()).load(searchData.icon.replace("http", "https"))
                .into(holder.hotTypeImageView)
        }
    }

    class HotSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotTypeImageView: ImageView
        val showNameText: TextView
        init {
            hotTypeImageView = itemView.findViewById(R.id.hotType)
            showNameText = itemView.findViewById(R.id.usernameText)
        }
    }
}