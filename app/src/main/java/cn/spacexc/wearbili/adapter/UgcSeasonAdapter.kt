package cn.spacexc.wearbili.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.video.VideoPlayerActivity
import cn.spacexc.wearbili.dataclass.VideoPagesData

/**
 * Created by XC-Qan on 2022/7/3.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class UgcSeasonAdapter(val bvid: String) :
    ListAdapter<VideoPagesData, UgcSeasonAdapter.VideoPartViewHolder>(object :
        DiffUtil.ItemCallback<VideoPagesData>() {
        override fun areItemsTheSame(oldItem: VideoPagesData, newItem: VideoPagesData): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: VideoPagesData, newItem: VideoPagesData): Boolean {
            return false
        }

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPartViewHolder {
        return VideoPartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_video_part, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoPartViewHolder, position: Int) {
        holder.partName.text = "P${position + 1} - ${getItem(position).part}"
        holder.itemView.setOnClickListener {
            val intent =
                Intent(Application.getContext(), VideoPlayerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("videoCid", getItem(position).cid)
            intent.putExtra("videoBvid", bvid)
            intent.putExtra("videoTitle", "P${position + 1} - ${getItem(position).part}")
            Application.getContext().startActivity(intent)
        }
    }

    class VideoPartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partName: TextView

        init {
            partName = itemView.findViewById(R.id.partName)
        }
    }
}