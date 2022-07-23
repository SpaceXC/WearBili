package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import com.bumptech.glide.Glide


/**
 * Created by XC-Qan on 2022/6/16.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CommentAdapter(val lifeCycleScope: LifecycleCoroutineScope) :
    ListAdapter<CommentContentData, CommentAdapter.VideoCommentViewHolder>(object :
        DiffUtil.ItemCallback<CommentContentData>() {
        override fun areItemsTheSame(
            oldItem: CommentContentData,
            newItem: CommentContentData
        ): Boolean {
            return oldItem.rpid == newItem.rpid
        }

        override fun areContentsTheSame(
            oldItem: CommentContentData,
            newItem: CommentContentData
        ): Boolean {
            return oldItem.content?.message == newItem.content?.message && oldItem.member?.uname == newItem.member?.uname
        }

    }) {

    var uploaderMid: Long? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCommentViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        return VideoCommentViewHolder(
            inflater.inflate(
                R.layout.cell_comment_list,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoCommentViewHolder, position: Int) {
        //println(itemCount)
        val comment = getItem(position)
        println(position)
        holder.userName.text = comment.member!!.uname
        if (comment.member!!.vip.nickname_color.isNotEmpty()) holder.userName.setTextColor(
            Color.parseColor(
                comment.member!!.vip.nickname_color
            )
        )
        //holder.userLevel.text = "LV${comment.member!!.level_info.current_level}"
        holder.pubDate.text = (comment.ctime * 1000).toDateStr("yyyy-MM-dd")
        //TODO 表情包解析 WIP
        //var commentStr: String?

        comment.content?.emote?.forEach {
            comment.content?.message = comment.content?.message?.replace(
                it.key,
                "<img src=\"${it.value.url.replace("http", "https")}\"/>"
            )!!
        }
/*        for((key, value) in comment.content?.emote!!){
            val imgTag = "<img src=\"${value.url}\"/>"
            comment.content?.message?.replace(key, imgTag)
        }*/
        holder.content.text = comment.content?.message
/*        lifeCycleScope.launch {
            kotlin.runCatching {
                *//*val formattedText = Html.fromHtml(commentStr, Html.FROM_HTML_MODE_COMPACT,{ p0 ->
                    val byteArray = NetworkUtils.getUrlWithoutCallback(p0!!).body?.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size!!)
                    BitmapDrawable(bitmap)
                }) { _, _, _, _ -> }
                holder.content.text = formattedText*//*
            }


        }*/

        //holder.content.text = Html.fromHtml(commentStr)

        holder.likes.text = comment.like.toShortChinese()
        if (comment.member!!.mid == uploaderMid) {
            holder.isUp.visibility = View.VISIBLE
        }
        Glide.with(Application.getContext()).load(comment.member!!.avatar.replace("http", "https"))
            .circleCrop()
            .into(holder.avatar)

    }

    class VideoCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView
        var userName: TextView

        //var userLevel : TextView
        var pubDate: TextView
        var content: TextView
        var likes: TextView
        var isUp: TextView

        init {
            avatar = itemView.findViewById(R.id.dynamicAvatar)
            userName = itemView.findViewById(R.id.dynamicUsername)
            //userLevel = itemView.findViewById(R.id.commentUserLevel)
            pubDate = itemView.findViewById(R.id.dynamicPubDate)
            content = itemView.findViewById(R.id.dynamicText)
            likes = itemView.findViewById(R.id.likes)
            isUp = itemView.findViewById(R.id.isUp)
        }
    }
}