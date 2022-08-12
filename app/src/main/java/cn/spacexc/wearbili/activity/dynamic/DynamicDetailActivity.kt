package cn.spacexc.wearbili.activity.dynamic

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.adapter.*
import cn.spacexc.wearbili.databinding.ActivityDynamicDetailBinding
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.VideoComment
import cn.spacexc.wearbili.dataclass.dynamic.Detail
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ToastUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class DynamicDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDynamicDetailBinding
    var page: Int = 1
    var prevList = listOf<CommentContentData>()
    var adapter: CommentAdapter = CommentAdapter(lifecycleScope, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getLongExtra("dynamicId", 0)
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDynamicDetails(id.toString())
        }
        binding.comments.layoutManager = LinearLayoutManager(this)
        binding.comments.adapter = adapter
        binding.swipeRefreshLayout.isRefreshing = true
        getDynamicDetails(id.toString())
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        binding.pageName.setOnClickListener { finish() }
    }

    private fun getDynamicDetails(dynamicId: String) {
        DynamicManager.getDynamicDetails(dynamicId, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    binding.swipeRefreshLayout.isRefreshing = false
                    ToastUtils.makeText("网络异常").show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body?.string()
                val result = Gson().fromJson(str, Detail::class.java)
                val card = DynamicManager.dynamicProcessor(result.data.card)!!
                MainScope().launch {
                    binding.dynamicUsername.text = card.desc.user_profile.info.uname
                    binding.dynamicPubDate.text =
                        (card.desc.timestamp * 1000).toDateStr("MM-dd HH:mm")
                    binding.likes.text = card.desc.like.toShortChinese()
                    binding.replies.text = "回复(${card.desc.comment ?: 0.toShortChinese()})"
                    when (card.desc.type) {
                        1 -> {
                            getDynamicComments(1, card.desc.dynamic_id)
                            if ((card.cardObj as ForwardShareCard).item.content.isNullOrEmpty()) {
                                binding.dynamicText.text = "分享动态"
                            } else {
                                Thread {
                                    val sp = Html.fromHtml(
                                        topicProcessor(
                                            emojiProcessor(
                                                (card.cardObj as ForwardShareCard).item.content,
                                                card.display.emoji_info?.emoji_details
                                            ), card.display.topic_info?.topic_details
                                        ),
                                        NetworkUtils.imageGetter(binding.dynamicText.lineHeight),
                                        null
                                    )
                                    binding.dynamicText.post {
                                        binding.dynamicText.text = sp
                                    }
                                }.start()
                            }
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(this@DynamicDetailActivity)
                            binding.recyclerView.adapter = ForwardShareDynamicAdapter(
                                this@DynamicDetailActivity,
                                card.display.origin?.emoji_info?.emoji_details,
                                card.display.origin?.topic_info?.topic_details
                            ).apply {
                                submitList(
                                    listOf((card.cardObj as ForwardShareCard))
                                )
                            }
                        }
                        2 -> {
                            getDynamicComments(2, card.desc.rid)
                            if ((card.cardObj as ImageCard).item.description.isNullOrEmpty()) {
                                binding.dynamicText.text = "分享图片"
                            } else {
                                Thread {
                                    val sp = Html.fromHtml(
                                        topicProcessor(
                                            emojiProcessor(
                                                (card.cardObj as ImageCard).item.description,
                                                card.display.emoji_info?.emoji_details
                                            ), card.display.topic_info?.topic_details
                                        ),
                                        NetworkUtils.imageGetter(binding.dynamicText.lineHeight),
                                        null
                                    )
                                    binding.dynamicText.post {
                                        binding.dynamicText.text = sp
                                    }
                                }.start()
                            }
                            binding.relativeLayout.visibility = View.VISIBLE
                            val imageList = (card.cardObj as ImageCard).item.pictures
                            if (imageList.size < 3) binding.recyclerView.layoutManager =
                                GridLayoutManager(this@DynamicDetailActivity, imageList.size)
                            else binding.recyclerView.layoutManager =
                                GridLayoutManager(this@DynamicDetailActivity, 3)
                            binding.recyclerView.adapter =
                                DynamicImageAdapter(this@DynamicDetailActivity).apply {
                                    submitList(imageList); Log.d(
                                    Application.TAG,
                                    "onBindViewHolder: ${(card.cardObj as ImageCard).item.description}: 图片列表：$imageList"
                                )
                                }
                        }
                        4 -> {
                            getDynamicComments(4, card.desc.dynamic_id)
                            Thread {
                                val sp = Html.fromHtml(
                                    topicProcessor(
                                        emojiProcessor(
                                            (card.cardObj as TextCard).item.content,
                                            card.display.emoji_info?.emoji_details
                                        ), card.display.topic_info?.topic_details
                                    ),
                                    NetworkUtils.imageGetter(binding.dynamicText.lineHeight),
                                    null
                                )
                                binding.dynamicText.post {
                                    binding.dynamicText.text = sp
                                }
                            }.start()
                            binding.recyclerView.visibility = View.GONE
                        }
                        8 -> {
                            getDynamicComments(8, (card.cardObj as VideoCard).aid)
                            if ((card.cardObj as VideoCard).dynamic.isNullOrEmpty()) {
                                binding.dynamicText.text = "投稿视频"
                            } else {
                                Thread {
                                    val sp = Html.fromHtml(
                                        topicProcessor(
                                            emojiProcessor(
                                                (card.cardObj as VideoCard).dynamic,
                                                card.display.emoji_info?.emoji_details
                                            ), card.display.topic_info?.topic_details
                                        ),
                                        NetworkUtils.imageGetter(binding.dynamicText.lineHeight),
                                        null
                                    )
                                    binding.dynamicText.post {
                                        binding.dynamicText.text = sp
                                    }
                                }.start()
                            }
                            binding.replies.text =
                                "回复(${(card.cardObj as VideoCard).stat.reply.toShortChinese()})"
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(this@DynamicDetailActivity)
                            binding.recyclerView.adapter =
                                DynamicVideoAdapter(this@DynamicDetailActivity).apply {
                                    submitList(
                                        listOf((card.cardObj as VideoCard))
                                    )
                                }
                        }
                        else -> binding.dynamicText.text = "不支持的动态类型：${card.desc.type}"
                    }
                    try {
                        Glide.with(this@DynamicDetailActivity)
                            .load(card.desc.user_profile.info.face)
                            .circleCrop()
                            .into(binding.dynamicAvatar)
                    } catch (e: OutOfMemoryError) {

                    }
                }
            }

        })
        binding.dynamicText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getDynamicComments(type: Int, oid: Long) {
        /**
         * 1 - 转发
         * 2 - 图文
         * 4 - 文字
         * 8 - 投稿
         */
        val requestType = when (type) {
            1 -> 17
            2 -> 11
            4 -> 17
            8 -> 1
            else -> 0
        }
        DynamicManager.getCommentsByLikes(
            requestType,
            oid,
            page,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        binding.swipeRefreshLayout.isRefreshing = false
                        ToastUtils.makeText(
                            "网络异常"
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string()
                    val comments = Gson().fromJson(str, VideoComment::class.java)
                    MainScope().launch {
                        binding.swipeRefreshLayout.isRefreshing = false
                        if (comments.code == 0) {
                            val replies: MutableList<CommentContentData> =
                                comments.data.replies?.toMutableList()
                                    ?: mutableListOf()
                            binding.commentCount.text =
                                "评论(${comments.data.cursor.all_count.toShortChinese()})"
                            if (replies != prevList) {
                                prevList = replies
                                if (comments.data.top.member != null && comments.data.top.content != null) {
                                    val top = comments.data.top
                                    top.is_top = true
                                    replies.remove(comments.data.top)
                                    val topList = mutableListOf(top)

                                    val finalList = topList + replies
                                    adapter.submitList(finalList)
                                } else {
                                    adapter.submitList(adapter.currentList + replies)

                                }
                                page++
                                binding.swipeRefreshLayout.isRefreshing = false
                            } else {
                                //ToastUtils.makeText(requireContext(), "再怎么翻都没有啦", Toast.LENGTH_SHORT).show()
                            }


                            /*if(comments.data.cursor.is_begin){
                                val replies : MutableList<CommentContentData> = comments.data.replies.toMutableList()

                                if(comments.data.top_replies != null){
                                    val top = mutableListOf(comments.data.top_replies)
                                    replies.remove(comments.data.top_replies)
                                    adapter.submitList(top + replies)
                                }
                                else{
                                    adapter.submitList(replies)

                                }

    //                                println(adapter.currentList)
                            }
                            else{
                                if(comments.data.cursor.is_end) isEnd = true
                                adapter.submitList(adapter.currentList + comments.data.replies)
                            }*/

                        } else {
                            ToastUtils.makeText(
                                "评论加载失败啦"
                            ).show()
                        }
                    }
                }
            }
        )
    }
}