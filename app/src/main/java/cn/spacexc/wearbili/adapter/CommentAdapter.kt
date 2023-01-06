package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView


/**
 * Created by XC-Qan on 2022/6/16.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

const val TYPE_SEND = 0
const val TYPE_SHOW = 1

class CommentAdapter(
    val lifeCycleScope: LifecycleCoroutineScope,
    val context: Context,
    private val onSendComment: () -> Unit
) :
    ListAdapter<CommentContentData, RecyclerView.ViewHolder>(object :
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

    var uploaderMid: Long? = 0L

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return if (position == 0) TYPE_SEND
        else TYPE_SHOW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SHOW) VideoCommentViewHolder(
            inflater.inflate(
                R.layout.cell_comment_list,
                parent,
                false
            )
        )
        else EditAndSendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_edit_button, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //println(itemCount)
        if (holder is VideoCommentViewHolder) {
            val comment = getItem(position)
            holder.avatar.addClickScale()
            holder.avatar.setOnClickListener {
                Intent(context, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", comment.member?.mid)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
            holder.userName.addClickScale()
            holder.userName.setOnClickListener {
                Intent(context, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", comment.member?.mid)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
            holder.userName.text = comment.member!!.uname
            holder.upLiked.isVisible = comment.up_action.like
            if (comment.replies != null) {
                val hotRepliesAdapter = CommentHotRepliesAdapter(lifeCycleScope, uploaderMid)
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
            else {
                holder.userName.setTextColor(Color.WHITE)
            }

            holder.pubDate.text = (comment.ctime * 1000).toDateStr("yyyy-MM-dd")


            comment.content?.emote?.forEach {
                comment.content?.message = comment.content?.message?.replace(
                    it.key,
                    "<img src=\"${it.value.url}\"/>"
                )!!
            }
            comment.content?.message?.replace("\\n", "<br>")

            Thread {
                val sp = Html.fromHtml(
                    comment.content?.message,
                    NetworkUtils.imageGetter(holder.content.lineHeight),
                    null
                )
                holder.content.post {
                    holder.content.text = sp
                }
            }.start()


            holder.likes.text = comment.like.toShortChinese()
            holder.isUp.isVisible = comment.member?.mid == uploaderMid
            Glide.with(Application.getContext())
                .load(comment.member!!.avatar)
                .circleCrop()
                .into(holder.avatar)
        }
        if (holder is EditAndSendViewHolder) {
            holder.textView.text = "发一条友善的评论~"
            holder.cardView.setOnClickListener {
                onSendComment.invoke()
            }
            holder.cardView.addClickScale()
        }

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
        var upLiked: TextView

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
            upLiked = itemView.findViewById(R.id.upLiked)
        }
    }

    class EditAndSendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView
        val textView: TextView

        init {
            cardView = itemView.findViewById(R.id.cardView)
            textView = itemView.findViewById(R.id.editInfo)
        }
    }
}

class CommentHotRepliesAdapter(
    val lifeCycleScope: LifecycleCoroutineScope,
    private val upMid: Long?
) :
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
                "<img src=\"${it.value.url}\"/>"
            )!!
        }

        Thread {
            val sp = Html.fromHtml(
                "${reply.member.uname}:${reply.content?.message}",
                NetworkUtils.imageGetter(holder.textview.lineHeight),
                null
            ).toSpannable()
            sp.setSpan(
                ForegroundColorSpan(if (reply.member.mid == upMid) Color.parseColor("#fb7299") else Color.WHITE),
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