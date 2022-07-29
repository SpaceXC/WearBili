package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.utils.NetworkUtils
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

class CommentAdapter(val lifeCycleScope: LifecycleCoroutineScope, val context: Context) :
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
        holder.userName.text = comment.member!!.uname
        if (comment.replies != null) {
            val hotRepliesAdapter = CommentHotRepliesAdapter(lifeCycleScope)
            holder.replies.layoutManager = LinearLayoutManager(this@CommentAdapter.context)
            holder.replies.adapter = hotRepliesAdapter
            hotRepliesAdapter.submitList(comment.replies!!.toList())

            holder.repliesControl.text = comment.reply_control.sub_reply_entry_text
        } else {
            holder.repliesCard.visibility = View.GONE
        }
        if (comment.member!!.vip.nickname_color.isNotEmpty()) holder.userName.setTextColor(
            Color.parseColor(
                comment.member!!.vip.nickname_color
            )
        )

        holder.pubDate.text = (comment.ctime * 1000).toDateStr("yyyy-MM-dd")


        comment.content?.emote?.forEach {
            comment.content?.message = comment.content?.message?.replace(
                it.key,
                "<img src=\"${it.value.url.replace("http", "https")}\"/>"
            )!!
        }

        Thread {
            val sp = Html.fromHtml(
                comment.content?.message,
                NetworkUtils.imageGetter(holder.content.lineHeight + 5),
                null
            )
            holder.content.post {
                holder.content.text = sp
            }
        }.start()


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
        var replies: RecyclerView
        var repliesControl: TextView
        var repliesCard: LinearLayout

        init {
            avatar = itemView.findViewById(R.id.dynamicAvatar)
            userName = itemView.findViewById(R.id.dynamicUsername)
            //userLevel = itemView.findViewById(R.id.commentUserLevel)
            pubDate = itemView.findViewById(R.id.dynamicPubDate)
            content = itemView.findViewById(R.id.dynamicText)
            likes = itemView.findViewById(R.id.likes)
            isUp = itemView.findViewById(R.id.isUp)
            replies = itemView.findViewById(R.id.repliesList)
            repliesControl = itemView.findViewById(R.id.repliesControl)
            repliesCard = itemView.findViewById(R.id.repliesCard)
        }
    }
}

class CommentHotRepliesAdapter(val lifeCycleScope: LifecycleCoroutineScope) :
    ListAdapter<CommentContentData.Replies, CommentHotRepliesViewHolder>(object :
        DiffUtil.ItemCallback<CommentContentData.Replies>() {
        override fun areItemsTheSame(
            oldItem: CommentContentData.Replies,
            newItem: CommentContentData.Replies
        ): Boolean {
            return oldItem.rpid == newItem.rpid
        }

        override fun areContentsTheSame(
            oldItem: CommentContentData.Replies,
            newItem: CommentContentData.Replies
        ): Boolean {
            return oldItem.content?.message == newItem.content?.message
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHotRepliesViewHolder {
        return CommentHotRepliesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_comment_hot_reply, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentHotRepliesViewHolder, position: Int) {
        val reply = getItem(position)
        reply.content?.emote?.forEach {
            reply.content?.message = reply.content?.message?.replace(
                it.key,
                "<img src=\"${it.value.url.replace("http", "https")}\"/>"
            )!!
        }

        Thread {
            val sp = Html.fromHtml(
                "${reply.member.uname}:${reply.content?.message}",
                NetworkUtils.imageGetter(holder.textview.lineHeight + 5),
                null
            ).toSpannable()
            sp.setSpan(
                ForegroundColorSpan(Color.WHITE),
                0,
                reply.member.uname.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            holder.textview.post {
                holder.textview.text = sp
            }
        }.start()
    }

}

class CommentHotRepliesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textview: TextView

    init {
        textview = itemView.findViewById(R.id.replyText)
    }
}