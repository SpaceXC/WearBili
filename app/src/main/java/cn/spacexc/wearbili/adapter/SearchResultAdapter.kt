package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
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
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.dataclass.VideoSearch
import cn.spacexc.wearbili.utils.NumberUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Created by XC-Qan on 2022/6/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SearchResultAdapter : ListAdapter<VideoSearch.SearchData.VideoSearchResult, SearchResultAdapter.VideoViewHolder>(object :
        DiffUtil.ItemCallback<VideoSearch.SearchData.VideoSearchResult>() {
        override fun areItemsTheSame(
            oldItem: VideoSearch.SearchData.VideoSearchResult,
            newItem: VideoSearch.SearchData.VideoSearchResult
        ): Boolean {
            return oldItem.bvid == newItem.bvid
        }

        override fun areContentsTheSame(
            oldItem: VideoSearch.SearchData.VideoSearchResult,
            newItem: VideoSearch.SearchData.VideoSearchResult
        ): Boolean {
            return oldItem.bvid == newItem.bvid
        }

    })
{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(
            inflater.inflate(
                R.layout.cell_video_list,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video : VideoSearch.SearchData.VideoSearchResult = getItem(position)
        holder.listVideoTitle.text = Html.fromHtml(video.title)
        holder.listUpName.text = video.author
        holder.listVideoViews.text = NumberUtils.num2Chinese(video.play)
        holder.itemView.setOnClickListener {
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", video.bvid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.getContext().startActivity(intent)
        }
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)


        Glide.with(Application.getContext()).load("https:${video.pic}").placeholder(R.drawable.placeholder).apply(options).into(holder.listCover)

        //GlideUtils.loadPicsFitWidth(Application.getContext(), "https:${video.pic}", R.drawable.placeholder, R.drawable.placeholder, holder.listCover)
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listCover : ImageView
        var listVideoTitle : TextView
        var listUpName : TextView
        var listVideoViews : TextView


        init {
            listCover = itemView.findViewById(R.id.listCover)
            listVideoTitle = itemView.findViewById(R.id.listVideoTitle)
            listUpName = itemView.findViewById(R.id.listUpName)
            listVideoViews = itemView.findViewById(R.id.listPlayTimes)
        }
    }
}