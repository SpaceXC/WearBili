package cn.spacexc.wearbili.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.image.PhotoViewActivity
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.activity.video.PlayOnPhoneActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoPlayerActivity
import cn.spacexc.wearbili.activity.video.ViewFullVideoPartsActivity
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.adapter.VideoPartsAdapter
import cn.spacexc.wearbili.databinding.FragmentVideoInfoBinding
import cn.spacexc.wearbili.dataclass.*
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoInfoFragment : Fragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!

    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var bvid: String? = ""
    var cid = 0L
    var videoTitle = ""

    lateinit var videoPartsAdapter: VideoPartsAdapter

    var isFollowed = false

    private lateinit var buttonsAdapter: ButtonsAdapter

    var isLiked: Boolean = false
    var isCoined: Boolean = false
    var isStared: Boolean = false

    var likes = 0L


    private val btnListUpperRow = MutableLiveData(
        mutableListOf(
            RoundButtonData(R.drawable.ic_baseline_play_circle_outline_24, "内建播放", "内建播放"),
            RoundButtonData(R.drawable.ic_outline_thumb_up_24, "点赞", "点赞"),
            RoundButtonData(
                R.drawable.ic_baseline_play_circle_outline_24,
                "小电视播放器播放",
                "小电视播放器播放"
            ),
            RoundButtonData(R.drawable.ic_outline_monetization_on_24, "投币", "投币"),
            RoundButtonData(R.drawable.ic_round_star_border_24, "收藏", "收藏"),
            RoundButtonData(R.drawable.ic_outline_thumb_down_24, "点踩", "点踩"),
            RoundButtonData(R.drawable.ic_baseline_history_24, "稍后再看", "稍后再看"),
            RoundButtonData(R.drawable.send_to_mobile, "手机观看", "手机观看"),
            RoundButtonData(R.drawable.cloud_download, "缓存", "缓存")
        )
    )


    init {
        Log.d(Application.getTag(), "VideoInfoFragmentLoaded")
    }

    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ),
                0
            )
        } else {
            playWithTaiWan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        playWithTaiWan()
                    }
                } else {
                    ToastUtils.makeText("请授予权限以写入H码").show()
                }
            }
        }
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

    private fun playWithTaiWan() {
        lifecycleScope.launch {
            try {
                bvid?.let {
                    VideoManager.getVideoUrl(it, cid, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            MainScope().launch {
                                ToastUtils.makeText(
                                    "网络异常"
                                ).show()

                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseString = response.body?.string()
                            val videoUrls: VideoStreamsFlv = Gson().fromJson(
                                responseString,
                                VideoStreamsFlv::class.java
                            )        //创建视频数据对象
                            MainScope().launch {
                                val path =
                                    File(Environment.getExternalStorageDirectory().absolutePath + "/HankMi/").absoluteFile
                                val file =
                                    File(Environment.getExternalStorageDirectory().absolutePath + "/HankMi/keydata.hmd").absoluteFile
                                Log.d(
                                    TAG,
                                    "onResponse: " + Environment.getExternalStorageDirectory().absolutePath + "/HankMi/cache/media"
                                )
                                if (!path.exists()) {
                                    path.mkdir()
                                    file.createNewFile()
                                }
                                file.writeText("hmmedia=[${videoUrls.data.durl[0].url}]")

                                val intent = Intent().apply {
                                    action = "com.hankmi.media"
                                    startActivity(this)
                                }
                            }

                        }

                    })
                }
            } catch (e: Exception) {
                MainScope().launch {
                    ToastUtils.makeText("视频加载失败")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            Application.getTag(),
            "onViewCreated: Video ID: ${(activity as VideoActivity).getId()}"
        )
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
                    "内建播放" -> {
                        val intent =
                            Intent(requireActivity(), VideoPlayerActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("videoBvid", bvid)
                        intent.putExtra("videoCid", cid)
                        intent.putExtra("videoTitle", videoTitle)
                        startActivity(intent)
                    }
                    "稍后再看" -> {
                        bvid?.let { it1 ->
                            VideoManager.addToView(it1, object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    MainScope().launch {


                                        ToastUtils.makeText(
                                            "网络异常"
                                        ).show()

                                    }
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val result = Gson().fromJson(
                                        response.body?.string(),
                                        SimplestUniversalDataClass::class.java
                                    )
                                    MainScope().launch {
                                        when (result.code) {
                                            0 -> {
                                                ToastUtils.makeText(
                                                    "添加成功"
                                                ).show()
                                            }
                                            90001 -> {
                                                ToastUtils.makeText(
                                                    "稍后再看列表已满"
                                                ).show()
                                            }
                                            90003 -> {
                                                ToastUtils.makeText(
                                                    "视频不见了"
                                                ).show()
                                            }

                                        }
                                    }
                                }

                            })
                        }
                    }
                    "小电视播放器播放" -> {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("wearbiliplayer://receive:8080/play?&bvid=$bvid&cid=$cid&aid=0")
                            )
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            ToastUtils.makeText("需要安装小电视播放器哦").show()
                        }
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
        //getVideoIsLiked()
        isVideoLiked()
        getVideo()
        getVideoParts()
    }

    private fun getVideoParts() {

        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoParts(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText(
                        "加载失败了"
                    ).show()

                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!isAdded) return
                val responseStr = response.body?.string()
                val result = Gson().fromJson(responseStr, VideoPages::class.java)
                MainScope().launch {
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

        })
    }

    private fun getVideo() {
        if (!isAdded) return
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoById(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText(
                        "加载失败了"
                    ).show()

                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val video = Gson().fromJson(response.body?.string(), VideoInfo::class.java)
                updateVideoFansStat(video)
                MainScope().launch {
                    if (response.code == 200 && video.code == 0) {
                        bvid = video.data.bvid
                        cid = video.data.cid
                        videoTitle = video.data.title
                        likes = video.data.stat.like
                        binding.relativeLayout.visibility = View.VISIBLE
                        (activity as VideoActivity).currentVideo = video.data
                        (activity as VideoActivity).isInitialized = true
                        binding.cover.setOnLongClickListener {
                            val intent =
                                Intent(requireActivity(), PhotoViewActivity::class.java)
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
                            intent.putExtra("videoTitle", videoTitle)
                            startActivity(intent)
                        }
                        binding.videoTitle.text = video.data.title
                        binding.bvidText.text = video.data.bvid
                        binding.duration.text = video.data.duration.secondToTime()
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
                        //isLikedStr.value = video.data.stat.like.toString()
                        (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).name.text =
                            video.data.stat.like.toShortChinese()
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
                            ToastUtils.makeText("已复制BV号")
                                .show()
                            true
                        }

                        binding.videoDesc.setOnLongClickListener {
                            val clipboardManager: ClipboardManager =
                                ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                            val clip: ClipData =
                                ClipData.newPlainText("wearbili desc", video.data.desc)
                            clipboardManager.setPrimaryClip(clip)
                            ToastUtils.makeText("已复制简介")
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

                        //GlideUtils.loadPicsFitWidth(Application.getContext(), video.data.pic, R.drawable.placeholder, R.drawable.placeholder, binding.cover)
                    } else {
                        ToastUtils.makeText("加载失败了")
                            .show()

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
                    MainScope().launch {
                        ToastUtils.makeText(
                            "加载失败了"
                        ).show()

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
                                    MainScope().launch {
                                        binding.uploaderFans.text =
                                            "${userFans.data.card.fans.toShortChinese()}粉丝"

                                    }
                                }

                            })
                        MainScope().launch {
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

            })
    }

    fun followUser(mid: Long, video: VideoInfo) {
        if (cn.spacexc.wearbili.manager.UserManager.getUserCookie() == null) {
            ToastUtils.makeText("你还没有登录哦").show()
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
                    MainScope().launch {


                        ToastUtils.makeText(
                            "关注失败了"
                        ).show()

                    }
                }

                override fun onResponse(call: Call, response: Response) {

                    MainScope().launch {
                        ToastUtils.makeText(
                            "关注成功了"
                        ).show()
                        updateVideoFansStat(video)
                    }


                }

            })
        } else {
            cn.spacexc.wearbili.manager.UserManager.deSubscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "取关失败了"
                        ).show()

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "取关成功了"
                        ).show()
                        updateVideoFansStat(video)
                    }

                }

            })
        }
    }
/*

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
                                //isLikedStr.value = "已点赞"
                                (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).name.text = "已点赞"

                            }
                        }
                    }
                }

            })
        }
    }
*/

    fun likeVideo() {
        if (!isAdded) return
        Log.d(TAG, "likeVideo: ")
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            ToastUtils.makeText("你还没有登录哦").show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }
        VideoManager.likeVideo(
            (activity as VideoActivity).getId()!!,
            isLiked,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText("网络异常").show()

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), Like::class.java)
                    when (result.code) {
                        0 -> {
                            MainScope().launch {
                                (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).apply {
                                    if (isLiked) {
                                        name.text = likes++.toShortChinese()

                                    } else {
                                        name.text = likes--.toShortChinese()

                                    }
                                }
                                isVideoLiked()
                            }
                        }
                        -101 -> {
                            MainScope().launch {
                                ToastUtils.makeText("你还没有登录哦").show()
                                val intent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                intent.putExtra("fromHome", false)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)

                            }
                        }
                        -111 -> {
                            MainScope().launch {
                                ToastUtils.makeText("验证错误，请请重新登录哦").show()
                                val intent =
                                    Intent(requireActivity(), LoginActivity::class.java)
                                intent.putExtra("fromHome", false)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        -10003 -> {

                            MainScope().launch {
                                ToastUtils.makeText("视频不见了").show()
                            }
                        }
                        else -> {
                            MainScope().launch {
                                ToastUtils.makeText("点赞失败 错误码${result.code}").show()
                            }
                        }
                    }
                }

            })
    }

    fun isVideoLiked() {
        VideoManager.isLiked((activity as VideoActivity).getId()!!, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText("网络异常").show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), Like::class.java)
                when (result.data) {
                    1 -> {
                        isLiked = true
                        MainScope().launch {
                            (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).apply {
                                icon.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                                icon.setColorFilter(Color.parseColor("#FE679A"))
                            }
                        }
                    }
                    0 -> {
                        isLiked = false
                        MainScope().launch {
                            (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).apply {
                                icon.setImageResource(R.drawable.ic_outline_thumb_up_24)
                                icon.setColorFilter(Color.parseColor("#FFFFFF"))
                            }
                        }
                    }
                    else -> {
                        MainScope().launch {
                            ToastUtils.makeText("网络异常 错误码${result.code}").show()
                        }
                    }
                }
            }

        })
    }

    data class Like(
        val code: Int,
        val data: Int?
    )
}