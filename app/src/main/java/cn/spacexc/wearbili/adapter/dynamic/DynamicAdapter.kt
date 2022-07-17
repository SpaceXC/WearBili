package cn.spacexc.wearbili.adapter.dynamic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.*
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.ImageViewerActivity
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.activity.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.Picture
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.VideoUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.wyx.components.widgets.ExpandCollpaseTextView

/**
 * Created by XC-Qan on 2022/7/16.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

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
        holder.pubDate.text = (card.desc.timestamp * 1000).toDateStr()
        holder.likes.text = card.desc.like.toShortChinese()
        holder.replies.text = "回复(${card.desc.comment ?: 0.toShortChinese()})"
        if (!card.desc.user_profile.vip.nickname_color.isNullOrEmpty()) holder.userName.setTextColor(
            Color.parseColor(card.desc.user_profile.vip.nickname_color)
        )
        try {
            Glide.with(Application.getContext()).load(card.desc.user_profile.info.face)
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
        when (card.desc.type) {
            1 -> {
                if ((card.cardObj as ForwardShareCard).item.content.isNullOrEmpty()) {
                    holder.content.setText("分享动态")
                } else {
                    holder.content.setText((card.cardObj as ForwardShareCard).item.content)

                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                //holder.recyclerView.adapter = Dynami(context).apply { submitList(listOf((card.cardObj as VideoCard))) }
                holder.recyclerView.adapter = ForwardShareDynamicAdapter(context).apply {
                    submitList(
                        listOf((card.cardObj as ForwardShareCard))
                    )
                }
            }
            2 -> {
                if ((card.cardObj as ImageCard).item.description.isNullOrEmpty()) {
                    holder.content.setText("分享图片")
                } else {
                    holder.content.setText((card.cardObj as ImageCard).item.description)
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
                holder.content.setText((card.cardObj as TextCard).item.content)
                holder.recyclerView.visibility = View.GONE
            }
            8 -> {
                if ((card.cardObj as VideoCard).dynamic.isNullOrEmpty()) {
                    holder.content.setText("投稿视频")
                } else {
                    holder.content.setText((card.cardObj as VideoCard).dynamic)
                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
                holder.recyclerView.adapter =
                    DynamicVideoAdapter(context).apply { submitList(listOf((card.cardObj as VideoCard))) }
            }
            else -> holder.content.setText("不支持的动态类型：${card.desc.type}")
        }
    }


}

class NormalDynamicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var avatar: ImageView
    var userName: TextView

    //var userLevel : TextView
    var pubDate: TextView
    var content: ExpandCollpaseTextView
    var likes: TextView
    var replies: TextView
    var recyclerView: RecyclerView
    val relativeLayout: RelativeLayout

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
        holder.listVideoTitle.text = video.title
        holder.listUpName.text = video.owner.name

        //holder.listVideoDuration.text = TimeUtils.secondToTime(video.duration.toLong())
        holder.cardView.setOnClickListener {
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", VideoUtils.av2bv("av${video.aid}"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.getContext().startActivity(intent)
        }
        holder.cardView.setOnLongClickListener {
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
                .placeholder(R.drawable.placeholder).apply(options).into(holder.listCover)
        } catch (e: OutOfMemoryError) {

        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listCover: ImageView
        var listVideoTitle: TextView
        var listUpName: TextView
        var cardView: MaterialCardView

        init {
            listCover = itemView.findViewById(R.id.listCover)
            listVideoTitle = itemView.findViewById(R.id.listVideoTitle)
            listUpName = itemView.findViewById(R.id.listUpName)
            cardView = itemView.findViewById(R.id.cardView)
        }
    }
}

class ForwardShareDynamicAdapter(val context: Context) :
    ListAdapter<ForwardShareCard, NormalDynamicViewHolder>(object :
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalDynamicViewHolder {
        return NormalDynamicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_forward_share_dynamic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NormalDynamicViewHolder, position: Int) {
        val card = getItem(position)
        holder.userName.text = card.origin_user.info.uname
        //holder.pubDate.text = card.ori
        if (!card.origin_user.vip.nickname_color.isNullOrEmpty()) holder.userName.setTextColor(
            Color.parseColor(
                card.origin_user.vip.nickname_color
            )
        )
        try {
            Glide.with(Application.getContext()).load(card.origin_user.info.face)
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
                    holder.content.setText("分享动态")
                } else {
                    holder.content.setText(card.item.content)

                }
                holder.recyclerView.visibility = View.VISIBLE
                holder.recyclerView.layoutManager = LinearLayoutManager(context)
            }
            is VideoCard -> {
                if ((card.originObj as VideoCard).dynamic.isNullOrEmpty()) {
                    holder.content.setText("投稿视频")
                } else {
                    holder.content.setText((card.originObj as VideoCard).dynamic)
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
                holder.content.setText(card.item.content)
                holder.recyclerView.visibility = View.GONE
            }
            is ImageCard -> {
                if ((card.originObj as ImageCard).item.description.isNullOrEmpty()) {
                    holder.content.setText("分享图片")
                } else {
                    holder.content.setText((card.originObj as ImageCard).item.description)
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
            else -> holder.content.setText("不支持的动态类型")

        }

    }


}
