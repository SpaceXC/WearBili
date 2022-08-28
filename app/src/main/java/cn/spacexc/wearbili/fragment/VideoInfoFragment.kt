package cn.spacexc.wearbili.fragment

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.image.PhotoViewActivity
import cn.spacexc.wearbili.activity.settings.ChooseSettingsActivity
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.activity.video.*
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.adapter.VideoPartsAdapter
import cn.spacexc.wearbili.databinding.FragmentVideoInfoBinding
import cn.spacexc.wearbili.dataclass.BaseData
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.SimplestUniversalDataClass
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.dataclass.videoDetail.VideoDetailInfo
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
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
import kotlin.math.roundToInt

class VideoInfoFragment : Fragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!


    var bvid: String? = ""
    var cid = 0L
    var videoTitle = ""

    var progress: Long? = 0

    lateinit var videoPartsAdapter: VideoPartsAdapter

    var isFollowed = false

    private lateinit var buttonsAdapter: ButtonsAdapter

    var isLiked: Boolean = false
    var isCoined: Boolean = false
    var isStared: Boolean = false

    var likes = 0L

    private var likeButton: ButtonsAdapter.ButtonViewHolder? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                playWithTaiWan()
            } else {
                ToastUtils.showText("请授予权限以调用抬腕视频")
            }
        }


    private val btnListUpperRow = MutableLiveData(
        mutableListOf(
            RoundButtonData(R.drawable.ic_baseline_play_circle_outline_24, "播放", "播放"),
            RoundButtonData(R.drawable.ic_outline_thumb_up_24, "点赞", "点赞"),
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
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
            kotlin.runCatching {
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            Application.getTag(),
            "onViewCreated: Video ID: ${(activity as VideoActivity).getId()}"
        )
        binding.recyclerViewButtons.layoutManager = GridLayoutManager(requireContext(), 3)
        buttonsAdapter = ButtonsAdapter(true, object : OnItemViewClickListener {
            override fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                when (buttonName) {
                    "手机观看" -> {
                        if (isAdded) {
                            val intent =
                                Intent(requireActivity(), PlayOnPhoneActivity::class.java)
                            intent.putExtra(
                                "qrCodeUrl",
                                "https://www.bilibili.com/video/${(activity as VideoActivity).getId()}"
                            )
                            startActivity(intent)
                            /*val intent = Intent(requireActivity(), ConfirmationActivity::class.java).apply {
                                putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                                putExtra(ConfirmationActivity.EXTRA_ANIMATION_DURATION_MILLIS, 10000)
                                putExtra(ConfirmationActivity.EXTRA_MESSAGE, "https://www.bilibili.com/video/${(activity as VideoActivity).getId()}")
                            }
                            startActivity(intent)*/
                        }
                    }
                    "点赞" -> {
                        likeVideo()
                    }
                    "播放" -> {
                        when (SettingsManager.defPlayer()) {
                            "builtinPlayer" -> {
                                val intent =
                                    Intent(requireActivity(), VideoPlayerActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("videoBvid", bvid)
                                intent.putExtra("videoCid", cid)
                                intent.putExtra("videoTitle", videoTitle)
                                intent.putExtra("progress", progress)
                                startActivity(intent)
                            }
                            "minifyPlayer" -> {
                                val intent =
                                    Intent(requireActivity(), MinifyVideoPlayer::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("videoBvid", bvid)
                                intent.putExtra("videoCid", cid)
                                intent.putExtra("videoTitle", videoTitle)
                                startActivity(intent)
                            }
                            "microTvPlayer" -> {
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
                            "microTaiwan" -> {
                                ToastUtils.showText("敬请期待")
                            }
                            "other" -> {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("wearbili-3rd://video/play?&bvid=$bvid&cid=$cid")
                                    )
                                    startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    ToastUtils.showText("没有找到其他播放器哦")
                                }
                            }
                        }
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
                }
            }

            override fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                when (buttonName) {
                    "播放" -> {
                        Log.d(TAG, "setOnLongClickListener: ")
                        val intent = Intent(context, ChooseSettingsActivity::class.java)
                        val item = SettingsManager.getSettingByName("defaultPlayer")
                        intent.putExtra("item", item)
                        /*intent.putExtra("itemKey", item?.settingName)
                        intent.putExtra("itemName", item?.displayName)
                        intent.putExtra("defVal", item?.defString)*/
                        startActivity(intent)
                    }
                }
            }

        }).also { it.submitList(btnListUpperRow.value) }
        btnListUpperRow.observe(viewLifecycleOwner) {
            buttonsAdapter.submitList(it)
            println(it)
        }
        likeButton =
            (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder?)

        binding.scrollview?.setOnGenericMotionListener { v, ev ->
            if (ev.action == MotionEvent.ACTION_SCROLL &&
                ev.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
            ) {
                // Don't forget the negation here
                val delta = -ev.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
                        ViewConfigurationCompat.getScaledVerticalScrollFactor(
                            ViewConfiguration.get(context), requireContext()
                        )
                // Swap these axes to scroll horizontally instead
                v.scrollBy(0, delta.roundToInt() * 10)
                true
            } else {
                false
            }
        }

        binding.recyclerViewButtons.adapter = buttonsAdapter
        binding.recyclerViewParts.layoutManager = LinearLayoutManager(requireContext())
        videoPartsAdapter = VideoPartsAdapter((activity as VideoActivity).getId()!!)
        binding.recyclerViewParts.adapter = videoPartsAdapter
        //binding.recyclerViewLower.adapter = ButtonsAdapter(true).also { it.submitList(btnListLowerRow) }
        //getVideoIsLiked()
        isVideoLiked()
        getInfo()
    }

    private fun getInfo() {
        if (!isAdded) return
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoInfo(id!!, object : NetworkUtils.ResultCallback<VideoDetailInfo> {
            override fun onSuccess(result: VideoDetailInfo, code: Int) {
                MainScope().launch {
                    result.log()
                    if (code == 0) {
                        updateVideoFansStat(result)
                        bvid = result.data.bvid
                        cid = result.data.cid
                        progress = (result.data.history?.progress ?: 0)
                        videoTitle = result.data.title
                        likes = result.data.stat.like
                        binding.relativeLayout.visibility = View.VISIBLE
                        (activity as VideoActivity).currentVideo = result.data
                        (activity as VideoActivity).isInitialized = true

                        videoPartsAdapter.submitList(result.data.pages)
                        binding.videoPartsTitle.isVisible = result.data.pages.size != 1
                        binding.recyclerViewParts.isVisible = result.data.pages.size != 1
                        binding.videoPartsTitle.setOnClickListener {
                            val intent = Intent(
                                requireActivity(),
                                ViewFullVideoPartsActivity::class.java
                            )
                            intent.putExtra(
                                "data",
                                Gson().toJson(cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(result.data.pages))
                            )
                            intent.putExtra("bvid", (activity as VideoActivity).getId())
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            Application.getContext().startActivity(intent)
                        }

                        binding.cover.setOnLongClickListener {
                            val intent =
                                Intent(requireActivity(), PhotoViewActivity::class.java)
                            intent.putExtra("imageUrl", result.data.pic)
                            startActivity(intent)
                            true
                        }
                        binding.cover.setOnClickListener {
                            //(activity as VideoActivity).setPage(2)
                            when (SettingsManager.defPlayer()) {
                                "builtinPlayer" -> {
                                    val intent =
                                        Intent(requireActivity(), VideoPlayerActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra("videoBvid", bvid)
                                    intent.putExtra("videoCid", cid)
                                    intent.putExtra("videoTitle", videoTitle)
                                    intent.putExtra("progress", progress)
                                    startActivity(intent)
                                }

                                "minifyPlayer" -> {
                                    val intent =
                                        Intent(requireActivity(), MinifyVideoPlayer::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra("videoBvid", bvid)
                                    intent.putExtra("videoCid", cid)
                                    intent.putExtra("videoTitle", videoTitle)
                                    startActivity(intent)
                                }

                                "microTvPlayer" -> {
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

                                "microTaiwan" -> {
                                    ToastUtils.showText("敬请期待")
                                }

                                "other" -> {
                                    try {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("wearbili-3rd://receive:8080/play?&bvid=$bvid&cid=$cid")
                                        )
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        ToastUtils.showText("没有找到其他播放器哦")
                                    }
                                }
                            }
                        }
                        binding.videoTitle.text = result.data.title
                        binding.bvidText.text = result.data.bvid
                        binding.duration.text = result.data.duration.secondToTime()
                        binding.uploaderName.text = result.data.owner.name
                        binding.danmakusCount.text = result.data.stat.danmaku.toShortChinese()
                        binding.viewsCount.text = result.data.stat.view.toShortChinese()
                        binding.pubdateText.text = (result.data.pubdate * 1000).toDateStr()
                        binding.videoDesc.setText(result.data.desc)
                        binding.follow.setOnClickListener {
                            followUser(result.data.owner.mid, result)
                        }
                        if (result.data.history?.progress == null) {
                            VideoManager.uploadVideoViewingProgress(
                                result.data.bvid,
                                result.data.cid,
                                0
                            )
                        }
                        //isLikedStr.value = video.data.stat.like.toString()
                        (binding.recyclerViewButtons.findViewHolderForAdapterPosition(1) as ButtonsAdapter.ButtonViewHolder).name.text =
                            result.data.stat.like.toShortChinese()
                        btnListUpperRow.value?.get(0)?.displayName =
                            result.data.stat.like.toShortChinese()
                        Glide.with(this@VideoInfoFragment).load(result.data.owner.face)
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
                                ClipData.newPlainText("wearbili bvid", result.data.bvid)
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
                                ClipData.newPlainText("wearbili desc", result.data.desc)
                            clipboardManager.setPrimaryClip(clip)
                            ToastUtils.makeText("已复制简介")
                                .show()
                            true
                        }
                        val roundedCorners = RoundedCorners(10)
                        val options = RequestOptions.bitmapTransform(roundedCorners)
                        Glide.with(requireContext()).load(result.data.pic)
                            .placeholder(R.drawable.placeholder).skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .apply(options)
                            .into(binding.cover)
                        binding.cover.addClickScale()
                        binding.follow.addClickScale()
                        binding.videoDesc.addClickScale()

                        //GlideUtils.loadPicsFitWidth(Application.getContext(), video.data.pic, R.drawable.placeholder, R.drawable.placeholder, binding.cover)
                    } else {
                        when (code) {
                            -404 -> {
                                ToastUtils.makeText("视频不见了")
                                    .show()
                            }

                            else -> ToastUtils.makeText("加载失败了").show()
                        }


                    }
                }
            }

            override fun onFailed(e: Exception) {
                MainScope().launch {
                    ToastUtils.makeText("网络异常")
                    e.stackTrace.debugToast()
                }
            }

        })
    }
    /*private fun getVideo() {
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
                val video = Gson().fromJson(response.body?.string(), VideoDetailInfo::class.java)
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

                        videoPartsAdapter.submitList(video.data.pages)
                        binding.videoPartsTitle.isVisible = video.data.pages.size != 1
                        binding.recyclerViewParts.isVisible = video.data.pages.size != 1
                        binding.videoPartsTitle.setOnClickListener {
                            val intent = Intent(
                                requireActivity(),
                                ViewFullVideoPartsActivity::class.java
                            )
                            intent.putExtra("data", Gson().toJson(cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(video.data.pages)))
                            intent.putExtra("bvid", (activity as VideoActivity).getId())
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            Application.getContext().startActivity(intent)
                        }

                        binding.cover.setOnLongClickListener {
                            val intent =
                                Intent(requireActivity(), PhotoViewActivity::class.java)
                            intent.putExtra("imageUrl", video.data.pic)
                            startActivity(intent)
                            true
                        }
                        binding.cover.setOnClickListener {
                            //(activity as VideoActivity).setPage(2)
                            when (SettingsManager.defPlayer()) {
                                "builtinPlayer" -> {
                                    val intent =
                                        Intent(requireActivity(), VideoPlayerActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra("videoBvid", bvid)
                                    intent.putExtra("videoCid", cid)
                                    intent.putExtra("videoTitle", videoTitle)
                                    startActivity(intent)
                                }
                                "minifyPlayer" -> {
                                    val intent =
                                        Intent(requireActivity(), MinifyVideoPlayer::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra("videoBvid", bvid)
                                    intent.putExtra("videoCid", cid)
                                    intent.putExtra("videoTitle", videoTitle)
                                    startActivity(intent)
                                }
                                "microTvPlayer" -> {
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
                                "microTaiwan" -> {
                                    ToastUtils.showText("敬请期待")
                                }
                                "other" -> {
                                    try {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("wearbili-3rd://receive:8080/play?&bvid=$bvid&cid=$cid")
                                        )
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        ToastUtils.showText("没有找到其他播放器哦")
                                    }
                                }
                            }
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
    }*/

    fun updateVideoFansStat(video: VideoDetailInfo) {
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

    fun followUser(mid: Long, video: VideoDetailInfo) {
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
                            "网络异常"
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                    MainScope().launch {
                        if (result.code == 0) {
                            ToastUtils.makeText(
                                "关注成功了"
                            ).show()
                            updateVideoFansStat(video)
                        } else {
                            ToastUtils.showText("关注失败了，错误码${result.code}")
                        }
                    }
                }

            })
        } else {
            cn.spacexc.wearbili.manager.UserManager.deSubscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "网络异常"
                        ).show()

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                    MainScope().launch {
                        if (result.code == 0) {
                            ToastUtils.makeText(
                                "取关成功了"
                            ).show()
                            updateVideoFansStat(video)
                        } else {
                            ToastUtils.showText("取关失败了，错误码${result.code}")
                        }
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