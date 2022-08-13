package cn.spacexc.wearbili.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.*
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.dynamic.DynamicDetailActivity
import cn.spacexc.wearbili.activity.image.ImageViewerActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.dynamic.EmojiDetail
import cn.spacexc.wearbili.dataclass.dynamic.TopicDetail
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.Picture
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.utils.CustomLinkMovementMethod
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.VideoUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Created by XC-Qan on 2022/7/16.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

fun emojiProcessor(content: String?, emojis: List<EmojiDetail>?): String? {
    var temp = content
    if (emojis != null) {
        for (emoji in emojis) {
            temp = temp?.replace(emoji.emoji_name, "<img src=\"${emoji.url}\"/>")
        }
    }
    return temp
}

fun topicProcessor(content: String?, topics: List<TopicDetail>?): String? {
    var temp = content
    if (topics != null) {
        for (topic in topics) {
            temp = temp?.replace(
                "#${topic.topic_name}#",
                "<a href=\"wearbili://search/result?keyword=${topic.topic_name}\">#${topic.topic_name}#</a>"
            )
        }
    }
    return temp
}

class DynamicAdapter(val context: Context) :
    ListAdapter<Card, NormalDynamicViewHolder>(object : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.desc.dynamic_id == newItem.desc.dynamic_id
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalDynamicViewHolder {
        return NormalDynamicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_normal_dynamic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NormalDynamicViewHolder, position: Int) {
        val card = getItem(position)
        holder.userName.text = card.desc.user_profile.info.uname
        holder.pubDate.text = (card.desc.timestamp * 1000).toDateStr("MM-dd HH:mm")
        holder.likes.text = card.desc.like.toShortChinese()
        holder.replies.text = "回复(${card.desc.comment ?: 0.toShortChinese()})"
        if (!card.desc.user_profile.vip.nickname_color.isNullOrEmpty()) holder.userName.setTextColor(
            Color.parseColor(card.desc.user_profile.vip.nickname_color)
        )
        try {
            Glide.with(context).load(card.desc.user_profile.info.face)
                .circleCrop()
                .into(holder.avatar)
            //holder.avatar.setImageURI(Uri.parse(card.desc.user_profile.info.face))
        } catch (e: OutOfMemoryError) {

        }

        holder.cardView.setOnClickListener {
            val intent = Intent(context, DynamicDetailActivity::class.java)
            intent.putExtra("dynamicId", card.desc.dynamic_id)
            context.startActivity(intent)
        }
        /**
         * 1 - 转发
         * 2 - 图文
         * 4 - 文字
         * 8 - 投稿
         */
        when (card.desc.type) {
            1 -> {
                if ((card.cardObj as ForwardShareCard).item.content.isNullOrEmpty()) {
                    holder.content.text = "分享动态"
                } else {
                    Thread {
                        val sp = Html.fromHtml(
                            topicProcessor(
                                emojiProcessor(
                                    (card.cardObj as ForwardShareCard).item.content,
                                    card.display.emoji_info?.emoji_details
                                ), card.display.topic_info?.topic_details
                            ),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()
                }
                Log.d(
                    TAG,
                    "onBindViewHolder: ${holder.recyclerView.findViewHolderForAdapterPosition(0)}"
                )
                holder.recyclerView.setOnClickListener {
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    intent.putExtra("dynamicId", card.desc.orig_dy_id)
                    context.startActivity(intent)

                }

                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                holder.recyclerView.adapter = ForwardShareDynamicAdapter(
                    context,
                    card.display.origin?.emoji_info?.emoji_details,
                    card.display.origin?.topic_info?.topic_details
                ).apply {
                    submitList(
                        listOf((card.cardObj as ForwardShareCard))
                    )
                }
            }
            2 -> {
                if ((card.cardObj as ImageCard).item.description.isNullOrEmpty()) {
                    holder.content.text = "分享图片"
                } else {
                    Thread {
                        val sp = Html.fromHtml(
                            topicProcessor(
                                emojiProcessor(
                                    (card.cardObj as ImageCard).item.description,
                                    card.display.emoji_info?.emoji_details
                                ), card.display.topic_info?.topic_details
                            ),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()
                }
                holder.relativeLayout.visibility = View.VISIBLE
                val imageList = (card.cardObj as ImageCard).item.pictures
                if (imageList.size < 3) holder.recyclerView.layoutManager =
                    GridLayoutManager(context, imageList.size)
                else holder.recyclerView.layoutManager = GridLayoutManager(context, 3)
                holder.recyclerView.adapter = DynamicImageAdapter(context).apply {
                    submitList(imageList); Log.d(
                    TAG,
                    "onBindViewHolder: ${(card.cardObj as ImageCard).item.description}: 图片列表：$imageList"
                )
                }
            }
            4 -> {
                Thread {
                    val sp = Html.fromHtml(
                        topicProcessor(
                            emojiProcessor(
                                (card.cardObj as TextCard).item.content,
                                card.display.emoji_info?.emoji_details
                            ), card.display.topic_info?.topic_details
                        ),
                        NetworkUtils.imageGetter(holder.content.lineHeight),
                        null
                    )
                    holder.content.post {
                        holder.content.text = sp
                    }
                }.start()
                holder.recyclerView.visibility = View.GONE
            }
            8 -> {
                if ((card.cardObj as VideoCard).dynamic.isNullOrEmpty()) {
                    holder.content.text = "投稿视频"
                } else {
                    Thread {
                        val sp = Html.fromHtml(
                            topicProcessor(
                                emojiProcessor(
                                    (card.cardObj as VideoCard).dynamic,
                                    card.display.emoji_info?.emoji_details
                                ), card.display.topic_info?.topic_details
                            ),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()
                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.replies.text =
                    "回复(${(card.cardObj as VideoCard).stat.reply.toShortChinese()})"
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                holder.recyclerView.adapter =
                    DynamicVideoAdapter(context).apply { submitList(listOf((card.cardObj as VideoCard))) }
            }
            else -> holder.content.text = "不支持的动态类型：${card.desc.type}"
        }
        holder.content.paint.apply {
            underlineColor = Color.TRANSPARENT
            underlineThickness = 0f
            isUnderlineText = false
        }

        holder.content.movementMethod = CustomLinkMovementMethod.getInstance(holder.cardView)
    }

}

class NormalDynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var avatar: ImageView
    var userName: TextView

    //var userLevel : TextView
    var pubDate: TextView
    var content: TextView

    var likes: TextView
    var replies: TextView
    var recyclerView: RecyclerView
    val relativeLayout: RelativeLayout
    val cardView: CardView

    init {
        avatar = itemView.findViewById(R.id.dynamicAvatar)
        userName = itemView.findViewById(R.id.dynamicUsername)
        //userLevel = itemView.findViewById(R.id.commentUserLevel)
        pubDate = itemView.findViewById(R.id.dynamicPubDate)
        content = itemView.findViewById(R.id.dynamicText)
        likes = itemView.findViewById(R.id.likes)
        replies = itemView.findViewById(R.id.replies)
        recyclerView = itemView.findViewById(R.id.recyclerView)
        relativeLayout = itemView.findViewById(R.id.dynamicImagesRelative)
        cardView = itemView.findViewById(R.id.cardView)
    }
}

class ForwardShareDynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var avatar: ImageView
    var userName: TextView

    //var userLevel : TextView
    var content: TextView
    var recyclerView: RecyclerView
    val relativeLayout: RelativeLayout
    val cardView: CardView

    init {
        avatar = itemView.findViewById(R.id.dynamicAvatar)
        userName = itemView.findViewById(R.id.dynamicUsername)
        //userLevel = itemView.findViewById(R.id.commentUserLevel)
        content = itemView.findViewById(R.id.dynamicText)
        recyclerView = itemView.findViewById(R.id.recyclerView)
        relativeLayout = itemView.findViewById(R.id.dynamicImagesRelative)
        cardView = itemView.findViewById(R.id.cardView)

    }
}

class DynamicImageAdapter(val context: Context) :
    ListAdapter<Picture, DynamicImageAdapter.DynamicImageViewHolder>(object :
        DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem.img_src == newItem.img_src
        }

        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem.img_src == newItem.img_src
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicImageViewHolder {
        return DynamicImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_dynamic_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DynamicImageViewHolder, position: Int) {
        val roundedCorners = RoundedCorners(10)
        val options = RequestOptions.bitmapTransform(roundedCorners)
        try {
            Glide.with(context).load(getItem(position).img_src).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).apply(options).into(holder.imageView)
        } catch (e: OutOfMemoryError) {

        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ImageViewerActivity::class.java)
            val arrayList = arrayListOf<Picture>()
            arrayList.addAll(currentList)
            intent.putParcelableArrayListExtra("imageList", arrayList)
            intent.putExtra("currentPhotoItem", position)
            context.startActivity(intent)
        }
    }

    class DynamicImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.dynamicImageView)
        }
    }
}

class DynamicVideoAdapter(val context: Context) :
    ListAdapter<VideoCard, DynamicVideoAdapter.VideoViewHolder>(object :
        DiffUtil.ItemCallback<VideoCard>() {
        override fun areItemsTheSame(oldItem: VideoCard, newItem: VideoCard): Boolean {
            return oldItem.aid == newItem.aid
        }

        override fun areContentsTheSame(oldItem: VideoCard, newItem: VideoCard): Boolean {
            return oldItem.title == newItem.title && oldItem.pic == newItem.pic
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_video_dynamic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video: VideoCard = getItem(position)
        holder.videoTitle.text = video.title
        holder.videoViews.text = video.stat.view.toShortChinese()

        //holder.listVideoDuration.text = TimeUtils.secondToTime(video.duration.toLong())
        holder.videoCover.setOnClickListener {
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", VideoUtils.av2bv("av${video.aid}"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.getContext().startActivity(intent)
        }
        holder.videoCover.setOnLongClickListener {
            val intent = Intent(context, VideoLongClickActivity::class.java)
            intent.putExtra("bvid", VideoUtils.av2bv("av${video.aid}"))
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
                .placeholder(R.drawable.placeholder).apply(options).into(holder.videoCover)
        } catch (e: OutOfMemoryError) {

        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoCover: ImageView
        val videoTitle: TextView
        val videoViews: TextView

        init {
            videoCover = itemView.findViewById(R.id.videoCover)
            videoTitle = itemView.findViewById(R.id.videoTitle)
            videoViews = itemView.findViewById(R.id.videoViews)
        }
    }
}

class ForwardShareDynamicAdapter(
    val context: Context,
    val emojis: List<EmojiDetail>?,
    val topics: List<TopicDetail>?
) :
    ListAdapter<ForwardShareCard, ForwardShareDynamicViewHolder>(object :
        DiffUtil.ItemCallback<ForwardShareCard>() {
        override fun areItemsTheSame(
            oldItem: ForwardShareCard,
            newItem: ForwardShareCard
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForwardShareCard,
            newItem: ForwardShareCard
        ): Boolean {
            return oldItem == newItem
        }


    }) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForwardShareDynamicViewHolder {
        return ForwardShareDynamicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_forward_share_dynamic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ForwardShareDynamicViewHolder, position: Int) {
        val card = getItem(position)
        holder.userName.text = card.origin_user?.info?.uname
        //holder.pubDate.text = card.ori
        if (!card.origin_user?.vip?.nickname_color.isNullOrEmpty()) holder.userName.setTextColor(
            Color.parseColor(
                card.origin_user?.vip?.nickname_color
            )
        )
        try {
            Glide.with(Application.getContext()).load(card.origin_user?.info?.face)
                .circleCrop()
                .into(holder.avatar)
            //holder.avatar.setImageURI(Uri.parse(card.desc.user_profile.info.face))
        } catch (e: OutOfMemoryError) {

        }
        /**
         * 1 - 转发
         * 2 - 图文
         * 4 - 文字
         * 8 - 投稿
         */
        when (card.originObj) {
            is ForwardShareCard -> {

                if (card.item.content.isNullOrEmpty()) {
                    holder.content.text = "分享动态"
                } else {
                    Thread {
                        val sp = Html.fromHtml(
                            topicProcessor(emojiProcessor(card.item.content, emojis), topics),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()

                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                holder.recyclerView.adapter =
                    ForwardShareDynamicAdapter(context, emojis, topics).also {
                        it.submitList(
                            listOf((card.originObj as ForwardShareCard))
                        )
                    }
            }
            is VideoCard -> {
                if ((card.originObj as VideoCard).dynamic.isNullOrEmpty()) {
                    holder.content.text = "投稿视频"
                } else {
                    Thread {
                        val sp = Html.fromHtml(

                            topicProcessor(
                                emojiProcessor(
                                    (card.originObj as VideoCard).dynamic,
                                    emojis
                                ), topics
                            ),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()
                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                holder.recyclerView.adapter = DynamicVideoAdapter(context).apply {
                    submitList(
                        listOf(
                            card.originObj as VideoCard
                        )
                    )
                }
            }
            is TextCard -> {
                Thread {
                    val sp = Html.fromHtml(
                        topicProcessor(emojiProcessor(card.item.content, emojis), topics),
                        NetworkUtils.imageGetter(holder.content.lineHeight),
                        null
                    )
                    holder.content.post {
                        holder.content.text = sp
                    }
                }.start()
                holder.recyclerView.visibility = View.GONE
            }
            is ImageCard -> {
                if ((card.originObj as ImageCard).item.description.isNullOrEmpty()) {
                    holder.content.text = "分享图片"
                } else {
                    Thread {
                        val sp = Html.fromHtml(
                            topicProcessor(
                                emojiProcessor(
                                    (card.originObj as ImageCard).item.description,
                                    emojis
                                ), topics
                            ),
                            NetworkUtils.imageGetter(holder.content.lineHeight),
                            null
                        )
                        holder.content.post {
                            holder.content.text = sp
                        }
                    }.start()
                }
                holder.relativeLayout.visibility = View.VISIBLE
                val imageList = (card.originObj as ImageCard).item.pictures
                if (imageList.size < 3) holder.recyclerView.layoutManager =
                    GridLayoutManager(context, imageList.size)
                else holder.recyclerView.layoutManager = GridLayoutManager(context, 3)
                holder.recyclerView.adapter = DynamicImageAdapter(context).apply {
                    submitList(imageList); Log.d(
                    TAG,
                    "onBindViewHolder: ${(card.originObj as ImageCard).item.description}: 图片列表：$imageList"
                )
                }
            }
            else -> holder.content.text = "不支持的动态类型"

        }
        holder.content.movementMethod = LinkMovementMethod.getInstance()
    }


}