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
import cn.spacexc.wearbili.dataclass.watchlater.WatchLaterListObject
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView

/**
 * Created by XC-Qan on 2022/7/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class WatchLaterAdapter(val context: Context) :
    ListAdapter<WatchLaterListObject, WatchLaterAdapter.WatchLaterViewHolder>(object :
        DiffUtil.ItemCallback<WatchLaterListObject>() {
        override fun areItemsTheSame(
            oldItem: WatchLaterListObject,
            newItem: WatchLaterListObject
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WatchLaterListObject,
            newItem: WatchLaterListObject
        ): Boolean {
            return oldItem == newItem
        }

    }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterViewHolder {
        return WatchLaterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cell_video_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WatchLaterViewHolder, position: Int) {
        val video: WatchLaterListObject = getItem(position)
        holder.listVideoTitle.text = video.title
        holder.listUpName.text = video.owner.name
        holder.listVideoViews.text = video.stat.view.toShortChinese()

        //holder.listVideoDuration.text = TimeUtils.secondToTime(video.duration.toLong())
        holder.cardView.setOnClickListener {
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", video.bvid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.getContext().startActivity(intent)
        }
        holder.cardView.setOnLongClickListener {
            val intent = Intent(context, VideoLongClickActivity::class.java)
            intent.putExtra("bvid", video.bvid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        }
        //GlideUtils.loadPicsFitWidth(Application.getContext(), video.pic, R.drawable.placeholder, R.drawable.placeholder, holder.listCover)
        //Glide设置图片圆角角度
        //Glide设置图片圆角角度
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)
        try {
            Glide.with(context).load(video.pic).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder).apply(options).into(holder.listCover)
        } catch (_: OutOfMemoryError) {

        }
        holder.cardView.addClickScale()
    }

    class WatchLaterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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