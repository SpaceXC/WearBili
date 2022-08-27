package cn.spacexc.wearbili.adapter

import android.content.Context
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
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.star.Media
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView

/**
 * Created by XC-Qan on 2022/8/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class StarFolderItemAdapter(private val context: Context) :
    ListAdapter<Media, StarFolderItemAdapter.StarFolderItemViewHolder>(object :
        DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.bvid == newItem.bvid
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.title == newItem.title && oldItem.upper.name == newItem.upper.name
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarFolderItemViewHolder {
        return StarFolderItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_video_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StarFolderItemViewHolder, position: Int) {
        val video = getItem(position)
        holder.listVideoTitle.text = video.title
        holder.listUpName.text = video.upper.name
        holder.listVideoViews.text = video.cnt_info.play.toShortChinese()
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)
        try {
            Glide.with(context).load(video.cover).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder).apply(options).into(holder.listCover)
        } catch (e: OutOfMemoryError) {

        }

        holder.cardView.setOnClickListener {
            if (video.attr == 0) {
                val intent = Intent(Application.getContext(), VideoActivity::class.java)
                intent.putExtra("videoId", video.bvid)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.getContext().startActivity(intent)
            }
            if (video.attr == 1) {
                ToastUtils.makeText("视频已失效").show()
            }
        }
        holder.cardView.setOnLongClickListener {
            if (video.attr == 0) {
                val intent = Intent(context, VideoLongClickActivity::class.java)
                intent.putExtra("bvid", video.bvid)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            } else {
                false
            }
        }
        holder.cardView.addClickScale()
    }

    class StarFolderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listCover: ImageView
        var listVideoTitle: TextView
        var listUpName: TextView
        var listVideoViews: TextView
        var cardView: MaterialCardView

        init {
            listCover = itemView.findViewById(R.id.listCover)
            listVideoTitle = itemView.findViewById(R.id.listVideoTitle)
            listUpName = itemView.findViewById(R.id.listUpName)
            listVideoViews = itemView.findViewById(R.id.listPlayTimes)
            cardView = itemView.findViewById(R.id.cardView)
        }
    }
}