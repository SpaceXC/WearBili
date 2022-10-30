package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.video.rcmd.Item
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView


/**
 * Created by XC-Qan on 2022/6/11.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class VideoPhoneEndRecommendListAdapter(val context: Context) :
    ListAdapter<Item, VideoPhoneEndRecommendListAdapter.VideoViewHolder>(object :
        DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(
            oldItem: Item,
            newItem: Item
        ): Boolean {
            return oldItem.player_args == newItem.player_args
        }

        override fun areContentsTheSame(
            oldItem: Item,
            newItem: Item
        ): Boolean {
            return (oldItem.title == newItem.title && oldItem.cover == newItem.cover)
        }

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(inflater.inflate(R.layout.cell_video_list, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video: Item = getItem(position)
        holder.listVideoTitle.text = video.title
        holder.listUpName.text = video.args.up_name
        holder.listVideoViews.text = video.cover_left_text_1

        holder.cardView.addClickScale()

        if (video.goto == "av") {
            holder.cardView.setOnClickListener {
                val intent = Intent(Application.getContext(), VideoActivity::class.java)
                intent.putExtra("videoId", VideoUtils.av2bv("av${video.player_args.aid}"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.getContext().startActivity(intent)

            }
            holder.cardView.setOnLongClickListener {
                val intent = Intent(context, VideoLongClickActivity::class.java)
                intent.putExtra("bvid", VideoUtils.av2bv("av${video.player_args.aid}"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            }
            holder.avatarIcon.isVisible = true
        }
        if (video.goto == "bangumi") {
            holder.cardView.setOnClickListener {
                val intent = Intent(Application.getContext(), BangumiActivity::class.java)
                intent.putExtra("id", video.param)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.getContext().startActivity(intent)
            }
            holder.avatarIcon.isVisible = false
            holder.listUpName.text = "  ${video.badge}" ?: ""
        }
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)
        try {
            Glide.with(context).load(video.cover)/*.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)*/
                .placeholder(R.drawable.placeholder).apply(options).into(holder.listCover)
        } catch (_: OutOfMemoryError) {

        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listCover: ImageView
        var listVideoTitle: TextView
        var listUpName: TextView
        var listVideoViews: TextView
        var cardView: MaterialCardView
        var avatarIcon = itemView.findViewById<ImageView>(R.id.avatarIcon)

        init {
            listCover = itemView.findViewById(R.id.listCover)
            listVideoTitle = itemView.findViewById(R.id.listVideoTitle)
            listUpName = itemView.findViewById(R.id.listUpName)
            listVideoViews = itemView.findViewById(R.id.listPlayTimes)
            cardView = itemView.findViewById(R.id.cardView)
        }
    }

}