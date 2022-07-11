package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.*
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.adapter.VideoPartsAdapter
import cn.spacexc.wearbili.databinding.FragmentVideoInfoBinding
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.VideoInfo
import cn.spacexc.wearbili.dataclass.VideoPages
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoInfoFragment : Fragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!

    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    lateinit var videoPartsAdapter: VideoPartsAdapter

    var isFollowed = false

    private lateinit var buttonsAdapter: ButtonsAdapter

    var isLiked: Boolean = false
    var isCoined: Boolean = false
    var isStared: Boolean = false

    private var isLikedStr: MutableLiveData<String> = MutableLiveData("点赞")
    private var isCoinedStr: MutableLiveData<String> = MutableLiveData("投币")
    private var isStaredStr: MutableLiveData<String> = MutableLiveData("收藏")

    private val btnListUpperRow = MutableLiveData(
        mutableListOf(
            RoundButtonData(R.drawable.ic_outline_thumb_up_24, "点赞", isLikedStr.value!!),
            RoundButtonData(R.drawable.ic_outline_monetization_on_24, "投币", isCoinedStr.value!!),
            RoundButtonData(R.drawable.ic_round_star_border_24, "收藏", isStaredStr.value!!),
            RoundButtonData(R.drawable.ic_outline_thumb_down_24, "点踩", "点踩"),
            RoundButtonData(R.drawable.ic_baseline_history_24, "稍后再看", "稍后再看"),
            RoundButtonData(R.drawable.send_to_mobile, "手机观看", "手机观看"),
            RoundButtonData(R.drawable.cloud_download, "缓存", "缓存")
        )
    )


    init {
        Log.d(Application.getTag(), "VideoInfoFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewButtons.layoutManager = GridLayoutManager(requireContext(), 3)
        buttonsAdapter = ButtonsAdapter(true, object : OnItemViewClickListener {
            override fun onClick(buttonName: String) {
                when (buttonName) {
                    "手机观看" -> {
                        if (isAdded) {
                            val intent =
                                Intent(requireActivity(), PlayOnPhoneActivity::class.java)
                            intent.putExtra(
                                "qrCodeUrl",
                                "https://www.bilibili.com/video/${(activity as VideoActivity).getId()}"
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            requireActivity().startActivity(intent)
                        }
                    }
                    "点赞" -> {
                        likeVideo()
                    }
                }
            }

        }).also { it.submitList(btnListUpperRow.value) }
        btnListUpperRow.observe(viewLifecycleOwner) {
            buttonsAdapter.submitList(it)
            println(it)
        }
        binding.recyclerViewButtons.adapter = buttonsAdapter
        binding.recyclerViewParts.layoutManager = LinearLayoutManager(requireContext())
        videoPartsAdapter = VideoPartsAdapter((activity as VideoActivity).getId()!!)
        binding.recyclerViewParts.adapter = videoPartsAdapter
        //binding.recyclerViewLower.adapter = ButtonsAdapter(true).also { it.submitList(btnListLowerRow) }
        getVideoIsLiked()
        getVideo()
        getVideoParts()
    }

    private fun getVideoParts() {
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoParts(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            Application.getContext(),
                            "加载失败了",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!isAdded) return
                val responseStr = response.body?.string()
                val result = Gson().fromJson(responseStr, VideoPages::class.java)
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        if (response.code == 200 && result.code == 0 && result.data.size != 1 or 0) {
                            videoPartsAdapter.submitList(result.data.toList())
                            binding.videoPartsTitle.visibility = View.VISIBLE
                            binding.recyclerViewParts.visibility = View.VISIBLE
                            binding.videoPartsTitle.setOnClickListener {
                                val intent = Intent(
                                    requireActivity(),
                                    ViewFullVideoPartsActivity::class.java
                                )
                                intent.putExtra("data", responseStr)
                                intent.putExtra("bvid", (activity as VideoActivity).getId())
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                Application.getContext().startActivity(intent)
                            }
                        }
                    }
                }
            }

        })
    }

    private fun getVideo() {
        if (!isAdded) return
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoById(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            Application.getContext(),
                            "加载失败了",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val video = Gson().fromJson(response.body?.string(), VideoInfo::class.java)
                updateVideoFansStat(video)
                mThreadPool.execute {
                    requireActivity().runOnUiThread{
                        if(response.code == 200 && video.code == 0){
                            (activity as VideoActivity).currentVideo = video.data
                            (activity as VideoActivity).isInitialized = true
                            binding.cover.setOnLongClickListener {
                                val intent = Intent(requireActivity(), PhotoViewActivity::class.java)
                                intent.putExtra("imageUrl", video.data.pic)
                                startActivity(intent)
                                true
                            }
                            binding.cover.setOnClickListener {
                                //(activity as VideoActivity).setPage(2)
                                val intent =
                                    Intent(requireActivity(), VideoPlayerActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("videoBvid", video.data.bvid)
                                intent.putExtra("videoCid", video.data.cid)
                                startActivity(intent)
                            }
                            binding.videoTitle.text = video.data.title
                            binding.bvidText.text = video.data.bvid
                            binding.duration.text = TimeUtils.secondToTime(video.data.duration)
                            binding.uploaderName.text = video.data.owner.name
                            binding.danmakusCount.text = video.data.stat.danmaku.toShortChinese()
                            binding.viewsCount.text = video.data.stat.view.toShortChinese()
                            binding.pubdateText.text = (video.data.pubdate * 1000).toDateStr()
                            binding.videoDesc.setText(video.data.desc)
                            binding.follow.setOnClickListener {
                                followUser(video.data.owner.mid, video)
                            }
                            VideoManager.uploadVideoViewingProgress(
                                video.data.bvid,
                                video.data.cid,
                                0
                            )
                            isLikedStr.value = video.data.stat.like.toString()
                            btnListUpperRow.value?.get(0)?.displayName =
                                video.data.stat.like.toShortChinese()
                            Glide.with(this@VideoInfoFragment).load(video.data.owner.face)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.default_avatar).circleCrop()
                                .into(binding.uploaderAvatar)


                            binding.bvidText.setOnLongClickListener {
                                val clipboardManager: ClipboardManager =
                                    ContextCompat.getSystemService(
                                        requireContext(),
                                        ClipboardManager::class.java
                                    ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili bvid", video.data.bvid)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(requireContext(), "已复制BV号", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }

                            binding.videoDesc.setOnLongClickListener {
                                val clipboardManager: ClipboardManager = ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili desc", video.data.desc)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(requireContext(), "已复制简介", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }
                            val roundedCorners = RoundedCorners(10)
                            val options = RequestOptions.bitmapTransform(roundedCorners)
                            Glide.with(requireContext()).load(video.data.pic)
                                .placeholder(R.drawable.placeholder).skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .apply(options)
                                .into(binding.cover)
                            //TODO
                            //GlideUtils.loadPicsFitWidth(Application.getContext(), video.data.pic, R.drawable.placeholder, R.drawable.placeholder, binding.cover)
                        }else{
                            Toast.makeText(requireContext(), "加载失败了", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }

        })
    }

    fun updateVideoFansStat(video: VideoInfo) {
        if (!isAdded) return
        cn.spacexc.wearbili.manager.UserManager.getUserById(
            video.data.owner.mid,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                Application.getContext(),
                                "加载失败了",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val uploader: User = Gson().fromJson(response.body?.string(), User::class.java)
                    if (isAdded) {
                        cn.spacexc.wearbili.manager.UserManager.getUserFans(
                            uploader.data.mid,
                            object : Callback {
                                override fun onFailure(call: Call, e: IOException) {}

                                override fun onResponse(call: Call, response: Response) {
                                    val userFans =
                                        Gson().fromJson(
                                            response.body?.string(),
                                            UserFans::class.java
                                        )
                                    mThreadPool.execute {
                                        requireActivity().runOnUiThread {
                                            binding.uploaderFans.text =
                                                "${userFans.data.card.fans.toShortChinese()}粉丝"
                                        }
                                    }
                                }

                            })
                        mThreadPool.execute {
                            requireActivity().runOnUiThread {
                                if (uploader.data.vip.type != 0 && uploader.data.vip.nickname_color.isNotEmpty()) {
                                    //binding.vipText.text = user.data.vip.label.text
                                    binding.uploaderName.setTextColor(Color.parseColor(uploader.data.vip.nickname_color))
                                    //binding.vipText.setTextColor(Color.parseColor(user.data.vip.label.bg_color))
                                }
                                isFollowed = uploader.data.is_followed
                                binding.follow.visibility = View.VISIBLE
                                if (uploader.data.is_followed) {
                                    binding.follow.setBackgroundResource(R.drawable.background_small_circle_grey)
                                    binding.follow.setImageResource(R.drawable.ic_baseline_done_24)
                                } else {
                                    binding.follow.setBackgroundResource(R.drawable.background_small_circle)
                                    binding.follow.setImageResource(R.drawable.ic_baseline_add_24)
                                }
                            }
                        }
                    }

                }

            })
    }

    fun followUser(mid: Long, video: VideoInfo) {
        if (cn.spacexc.wearbili.manager.UserManager.getUserCookie() == null) {
            Toast.makeText(requireContext(), "你还没有登录哦", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }
        if (!isAdded) return
        if (!isFollowed) {
            cn.spacexc.wearbili.manager.UserManager.subscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                Application.getContext(),
                                "关注失败了",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {

                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                Application.getContext(),
                                "关注成功了",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateVideoFansStat(video)
                        }
                    }


                }

            })
        } else {
            cn.spacexc.wearbili.manager.UserManager.deSubscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                Application.getContext(),
                                "取关失败了",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                Application.getContext(),
                                "取关成功了",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateVideoFansStat(video)
                        }
                    }
                }

            })
        }
    }

    private fun getVideoIsLiked() {
        if (!isAdded) return
        val bvid = (activity as VideoActivity).getId()
        if (bvid != null) {
            VideoManager.isLiked(bvid, object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), Like::class.java)
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            if (result.data == 1) {
                                isLiked = true
                                isLikedStr.value = "已点赞"
                            }
                        }
                    }
                }

            })
        }
    }

    fun likeVideo() {
        if (!isAdded) return
        VideoManager.likeVideo((activity as VideoActivity).getId()!!, !isLiked, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "点赞失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), Like::class.java)
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        if (result.code == 0) {
                            isLiked = true
                            isLikedStr.value = "已点赞"
                        }
                    }
                }
            }

        })
    }

    fun refreshVideoStat(list: List<RoundButtonData>) {
        println(btnListUpperRow)
        buttonsAdapter.submitList(list)
    }

    data class Like(
        val code: Int,
        val data: Int?
    )
}