package cn.spacexc.wearbili.activity.video

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.databinding.ActivityVideoPlayerBinding
import cn.spacexc.wearbili.dataclass.OnlineInfos
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.dataclass.subtitle.Subtitle
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.*
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import cn.spacexc.wearbili.viewmodel.OnSeekCompleteListener
import cn.spacexc.wearbili.viewmodel.PlayerStatus
import cn.spacexc.wearbili.viewmodel.VideoPlayerViewModel
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.loader.ILoader
import master.flame.danmaku.danmaku.loader.IllegalDataException
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.IDataSource
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.Inflater

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding

    private var isLoadingPanelShowed = false

    private var isSettingPanelShowing = false

    private val animIn = TranslateAnimation(150f, 0f, 0f, 0f)
    private val animOut = TranslateAnimation(0f, 150f, 0f, 0f)

    /**
     * From CSDN https://blog.csdn.net/libeifs/article/details/6630281
     */
    private var BatteryN //目前电量
            = 0
    private var BatteryV //电池电压
            = 0
    private var BatteryT //电池温度
            = 0.0
    private var BatteryStatus //电池状态
            : String? = null
    private var BatteryTemp //电池使用情况
            : String? = null


    val viewModel by viewModels<VideoPlayerViewModel>()

    @SuppressLint("SourceLockedOrientationActivity", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val video: VideoInfoData? = intent.getParcelableExtra("videoData")
        val videoBvid = intent.getStringExtra("videoBvid")
        val videoCid = intent.getLongExtra("videoCid", 0)
        val cacheId = intent.getStringExtra("videoCid")
        val videoTitle = intent.getStringExtra("videoTitle") ?: ""
        val subtitleUrl = intent.getStringExtra("subtitleUrl")
        val isCache = intent.getBooleanExtra("isCache", false)

        val progress = intent.getLongExtra("progress", 0L) * 1000
        viewModel.progress = progress

        binding.videoTitle.text = if (isCache) "${cacheId?.split("///")?.get(1)} - ${
            cacheId?.split("///")?.get(2)
        }" else videoTitle
        Log.d(Application.getTag(), "onCreate: BVID:$videoBvid, CID: $videoCid")
        videoBvid?.let {
            lifecycleScope.launch {
                VideoManager.getOnlineCount(videoBvid, videoCid, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call, response: Response) {
                        MainScope().launch {
                            val info = Gson().fromJson(
                                response.body?.string(),
                                OnlineInfos::class.java
                            )
                            binding.onlineCount.text = "${info.data?.total}人在看"

                        }
                    }

                })

                while (true) {
                    binding.watchStats?.text =
                        "电量$BatteryN% 温度${BatteryT * 0.1}°C ${TimeUtils.getCurrentTime()}"
                    delay(500)
                }
            }

        }


        // 注册一个系统 BroadcastReceiver，作为访问电池计量之用这个不能直接在AndroidManifest.xml中注册
        registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        viewModel.apply {
            danmakuView = binding.danmakuView       //赋值viewmodel弹幕视图
            seekBar = binding.progress
            progressText = binding.progressText
            statTextView = binding.loadingStatText
            //-----------LiveData监听区域⬇️-----------
            isLoadingPanelVisible.observe(this@VideoPlayerActivity) {
                if (isLoadingPanelShowed) {
                    binding.progressCircle.clearAnimation()
                    binding.progressCircle.isVisible = it
                    binding.progressCircle.requestLayout()
                    binding.progressCircle.invalidate()
                } else {
                    binding.progressCircle.isVisible = false
                    binding.loadingStatText.isVisible = it
                    binding.tips.isVisible = it
                    binding.loadingGif.isVisible = it
                    binding.loadingPanel.setBackgroundColor(Color.TRANSPARENT)
                    binding.loadingPanel.isVisible = it
                }


            }     //加载progress显示

            isVideoLoaded.observe(this@VideoPlayerActivity) {
                isLoadingPanelShowed = it
            }

            currentSubtitle.observe(this@VideoPlayerActivity) {
                binding.subtitleText.text = it
            }
            subtitleVisibility.observe(this@VideoPlayerActivity) {
                binding.subtitleText.isVisible = it
            }

            videoResolution.observe(this@VideoPlayerActivity) {
                binding.progress.max = mediaPlayer.duration.toInt()
            }
            bufferPercent.observe(this@VideoPlayerActivity) {
                binding.progress.secondaryProgress =
                    binding.progress.max * it / 100     //视频缓冲进度显示
            }
            //播放控制器显示/消失"
            controllerVisibility.observe(this@VideoPlayerActivity) {
                binding.controller.isVisible = it == View.VISIBLE
                binding.title.visibility = it
                binding.rotate.visibility = it
                binding.danmakuSwitch.visibility = it
                //binding.settingsButton.visibility = it
                binding.videoTitle.requestFocus()
                binding.watchStats?.isVisible = !(it == View.VISIBLE)
            }
            videoResolution.observe(this@VideoPlayerActivity) {
                //resizeVideo(it.first, it.second)
            }
            //在viewmodel监听播放状态，更改控制按钮图标
            playerStat.observe(this@VideoPlayerActivity) {
                when (it) {
                    PlayerStatus.PLAYING -> {
                        binding.control.setImageResource(R.drawable.round_pause_black)
                    }

                    else -> {
                        binding.control.setImageResource(R.drawable.play)

                    }
                }
            }
            //-----------LiveData监听区域⬆️-----------
        }
        viewModel.onSeekCompleteListener = object : OnSeekCompleteListener {
            override fun onSeek(progress: Long) {
                videoBvid?.let {
                    VideoManager.uploadVideoViewingProgress(
                        videoBvid,
                        videoCid,
                        (progress / 1000).toInt()
                    )
                }
            }

        }
        videoBvid?.let {
            uploadVideoViewingProgress(videoBvid, videoCid)

        }
        //-----------Layout视图监听区域⬇️️-----------
        /*binding.surfaceView.holder.addCallback(object :
            SurfaceHolder.Callback {     //surfaceview监听
            override fun surfaceCreated(p0: SurfaceHolder) {}
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                viewModel.mediaPlayer.setVideoSurfaceHolder(holder)      //设置视频显示surfaceview
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}
        })*/
        binding.playerView.player = viewModel.mediaPlayer

        binding.settingsButton.setOnClickListener {
            binding.settingsPanel.startAnimation(if (isSettingPanelShowing) animOut else animIn)
            isSettingPanelShowing = !isSettingPanelShowing
        }

        binding.danmakuSwitch.setOnClickListener {
            binding.danmakuView.isVisible = !binding.danmakuView.isVisible
            binding.danmakuSwitch.background =
                if (binding.danmakuView.isVisible) AppCompatResources.getDrawable(
                    this@VideoPlayerActivity,
                    R.drawable.background_pink
                ) else AppCompatResources.getDrawable(
                    this@VideoPlayerActivity,
                    R.drawable.background_grey
                )
        }

        //点击屏幕触发播放控制器显示
        binding.constraintLayout8.setOnClickListener {
            if (isSettingPanelShowing) {
                binding.settingsPanel.startAnimation(animOut)
                isSettingPanelShowing = !isSettingPanelShowing
            } else viewModel.toggleControllerVisibility()
        }
        /*{
            viewModel.toggleControllerVisibility()
        }*/
        //播放控制按钮修改播放状态
        binding.control.setOnClickListener {
            viewModel.togglePlayerStatus()
        }

        //旋转屏幕
        binding.rotate.setOnClickListener {
            when (requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    /*viewModel.videoResolution.value?.let {
                        resizeVideo(it.first, it.second)
                    }*/
                }

                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    /*viewModel.videoResolution.value?.let {
                        resizeVideo(it.second, it.first)
                    }*/
                }

                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> {
                    requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    /*viewModel.videoResolution.value?.let {
                        resizeVideo(it.first, it.second)
                    }*/

                }

                else -> Log.d(Application.TAG, "onCreate: $requestedOrientation")
            }

        }
        //TODO:快进3秒
        //TODO:快退3秒
        binding.progress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {     //进度条拖动监听
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.isPlaying = false
                //viewModel.togglePlayerPlayStat(PlayerStatus.Seeking)
                binding.progressText.text =
                    "${(progress / 1000).secondToTime()}/${(viewModel.mediaPlayer.duration / 1000).secondToTime()}"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                viewModel.isPlaying = false
                viewModel.canDisappear = false        //拖动进度条时控制器不可消失
                binding.danmakuView.pause()
                //playerViewModel.togglePlayerStatus()
                viewModel.mediaPlayer.pause()     //拖动时暂停视频
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.isPlaying = true
                viewModel.canDisappear = true     //松开进度条控制器可以消失
                //playerViewModel.togglePlayerStatus()
                //viewModel.mediaPlayer.play()     //视频开始
                //binding.danmakuView.resume()
                p0?.progress?.let { viewModel.playerSeekTo(it) }      //mediaplayer进度更改
            }
        })

        binding.back.setOnClickListener {
            finish()
        }
        binding.videoTitle.setOnClickListener { finish() }

        binding.control.addClickScale()
        binding.rotate.addClickScale()
        binding.danmakuSwitch.addClickScale()
        binding.settingsButton.addClickScale()
        //-----------Layout视图监听区域⬆️-----------

        //-----------弹幕相关区域⬇️️️-----------
        binding.loadingStatText.text = "${binding.loadingStatText.text}\n弹幕正在加载中...稍等哟(≧∀≦)ゞ"
        val danmakuContext: DanmakuContext = DanmakuContext.create()       //弹幕context

        val maxLinesPair = HashMap<Int, Int>()     //弹幕最多行数
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_LR] = 5

        val overlappingEnablePair = HashMap<Int, Boolean>()     //防弹幕重叠
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_LR] = true
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_BOTTOM] = true

        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 1f) //设置默认样式
            .setDuplicateMergingEnabled(true)      //合并重复弹幕
            .setScrollSpeedFactor(1.2f)     //弹幕速度
            .setScaleTextSize(0.6f)     //文字大小
            .setCacheStuffer(SpannedCacheStuffer()) // 图文混排使用SpannedCacheStuffer  设置缓存绘制填充器，默认使用{@link SimpleTextCacheStuffer}只支持纯文字显示, 如果需要图文混排请设置{@link SpannedCacheStuffer}如果需要定制其他样式请扩展{@link SimpleTextCacheStuffer}|{@link SpannedCacheStuffer}
            .setMaximumLines(maxLinesPair) //设置最大显示行数
            .preventOverlapping(overlappingEnablePair) //设置防弹幕重叠，null为允许重叠

        val loader: ILoader =
            DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)       //设置解析b站xml弹幕

        if (isCache) {
            Thread {
                var danmakuFile: File? = null
                var subtitleFile: File? = null
                if (DanmakuSharedPreferencesUtils.contains(
                        cacheId?.split("///")?.get(0) ?: ""
                    )
                ) {
                    danmakuFile = DanmakuSharedPreferencesUtils.getUrl(
                        cid = cacheId?.split("///")
                            ?.get(0)
                            ?: ""
                    )?.let { File(it) }
                }
                if (SubtitleSharedPreferencesUtils.contains(cacheId?.split("///")?.get(0) ?: "")) {
                    subtitleFile = SubtitleSharedPreferencesUtils.getUrl(
                        cid = cacheId?.split("///")
                            ?.get(0)
                            ?: ""
                    )
                        ?.let { File(it) }
                }
                if (danmakuFile != null) {
                    try {
                        val inputStream = FileInputStream(danmakuFile)
                        loader.load(inputStream)

                        val danmukuParser = BiliDanmukuParser()
                        val dataSource: IDataSource<*> = loader.dataSource
                        danmukuParser.load(dataSource)

                        binding.danmakuView.setCallback(object : DrawHandler.Callback {
                            override fun updateTimer(timer: DanmakuTimer?) {

                            }

                            override fun drawingFinished() {

                            }

                            override fun prepared() {
                                //-----------弹幕相关区域⬆️️️️-----------

                                //-----------视频相关区域⬇️-----------
                                MainScope().launch {
                                    binding.loadingStatText.text =
                                        "${binding.loadingStatText.text}\n正在查找视频缓存源"
                                }
                                val download = ExoPlayerUtils.getInstance(Application.context!!)
                                    .getDownloadManager().downloadIndex.getDownload(cacheId ?: "")
                                if (download != null) {
                                    MainScope().launch {
                                        if (subtitleFile != null) {
                                            binding.loadingStatText.text =
                                                "${binding.loadingStatText.text}\n找到字幕，正在加载字幕"
                                            try {
                                                val subtitleInputStream = subtitleFile.inputStream()
                                                val subtitleString =
                                                    String(subtitleInputStream.readBytes())
                                                val subtitleObject = Gson().fromJson(
                                                    subtitleString,
                                                    Subtitle::class.java
                                                )
                                                viewModel.subtitle = subtitleObject
                                                binding.loadingStatText.text =
                                                    "${binding.loadingStatText.text}\n字幕加载成功"
                                            } catch (e: Exception) {
                                                binding.loadingStatText.text =
                                                    "${binding.loadingStatText.text}\n字幕加载失败"
                                            }
                                        } else {
                                            binding.loadingStatText.text =
                                                "${binding.loadingStatText.text}\n没有已缓存的字幕"
                                        }
                                        binding.loadingStatText.text =
                                            "${binding.loadingStatText.text}\n视频正在加载中...马上就好！"
                                        viewModel.loadVideo(download.request.toMediaItem())
                                    }
                                } else {
                                    MainScope().launch {
                                        binding.loadingStatText.text =
                                            "${binding.loadingStatText.text}\n视频加载失败：无视频源(可能是缓存消失了?)"
                                    }
                                }

                                //-----------视频相关区域⬆️️️-----------
                            }
                        })
                        binding.danmakuView.prepare(danmukuParser, danmakuContext)      //准备弹幕
                        binding.danmakuView.enableDanmakuDrawingCache(true)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        MainScope().launch {
                            ToastUtils.makeText(
                                "加载弹幕失败！"
                            ).show()
                            binding.loadingStatText.text = "${binding.loadingStatText.text}\n弹幕加载失败"
                        }
                        MainScope().launch {
                            binding.loadingStatText.text =
                                "${binding.loadingStatText.text}\n正在查找视频缓存源"
                        }
                        val download = ExoPlayerUtils.getInstance(Application.context!!)
                            .getDownloadManager().downloadIndex.getDownload(cacheId ?: "")
                        if (download != null) {
                            MainScope().launch {
                                if (subtitleFile != null) {
                                    binding.loadingStatText.text =
                                        "${binding.loadingStatText.text}\n找到字幕，正在加载字幕"
                                    try {
                                        val subtitleInputStream = subtitleFile.inputStream()
                                        val subtitleString = String(subtitleInputStream.readBytes())
                                        val subtitleObject =
                                            Gson().fromJson(subtitleString, Subtitle::class.java)
                                        viewModel.subtitle = subtitleObject
                                        binding.loadingStatText.text =
                                            "${binding.loadingStatText.text}\n字幕加载成功"
                                    } catch (e: Exception) {
                                        binding.loadingStatText.text =
                                            "${binding.loadingStatText.text}\n字幕加载失败"
                                    }
                                } else {
                                    binding.loadingStatText.text =
                                        "${binding.loadingStatText.text}\n没有已缓存的字幕"
                                }
                                binding.loadingStatText.text =
                                    "${binding.loadingStatText.text}\n视频正在加载中...马上就好！"
                                viewModel.loadVideo(download.request.toMediaItem())
                            }
                        } else {
                            MainScope().launch {
                                binding.loadingStatText.text =
                                    "${binding.loadingStatText.text}\n视频加载失败：无视频源(可能是缓存消失了?)"
                            }
                        }

                    }
                } else {
                    MainScope().launch {
                        binding.loadingStatText.text =
                            "${binding.loadingStatText.text}\n没有找到已缓存的弹幕, 即将跳过"
                    }
                    MainScope().launch {
                        binding.loadingStatText.text =
                            "${binding.loadingStatText.text}\n正在查找视频缓存源"
                    }
                    val download = ExoPlayerUtils.getInstance(Application.context!!)
                        .getDownloadManager().downloadIndex.getDownload(cacheId ?: "")
                    if (download != null) {
                        MainScope().launch {
                            binding.loadingStatText.text =
                                "${binding.loadingStatText.text}\n视频正在加载中...马上就好！"
                            viewModel.loadVideo(download.request.toMediaItem())
                        }
                    } else {
                        MainScope().launch {
                            binding.loadingStatText.text =
                                "${binding.loadingStatText.text}\n视频加载失败：无视频源(可能是缓存消失了?)"
                        }
                    }
                }
            }.start()
        } else {
            VideoManager.getDanmaku(videoCid, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "加载弹幕失败！"
                        ).show()
                        binding.loadingStatText.text = "${binding.loadingStatText.text}\n弹幕加载失败"

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        //val responseIs = response.body?.byteStream()
                        //val str = String(decompress(responseIs.readBytes()).inputStream()!!)
                        //Log.d(Application.getTag(), "onResponse: ${response.body?.string()}")
                        val danmakuIs: ByteArray? = decompress(response.body?.bytes()!!)
                        loader.load(danmakuIs!!.inputStream())
                        Log.d(Application.getTag(), "onResponse: ${String(danmakuIs)}")
                    } catch (e: IllegalDataException) {
                        e.printStackTrace()
                        MainScope().launch {
                            ToastUtils.makeText(
                                "加载弹幕失败！"
                            ).show()
                            binding.loadingStatText.text = "${binding.loadingStatText.text}\n弹幕加载失败"


                        }
                    }
                    val danmukuParser = BiliDanmukuParser()
                    val dataSource: IDataSource<*> = loader.dataSource
                    danmukuParser.load(dataSource)

                    binding.danmakuView.setCallback(object : DrawHandler.Callback {
                        override fun updateTimer(timer: DanmakuTimer?) {

                        }

                        override fun drawingFinished() {

                        }

                        override fun prepared() {
                            //-----------弹幕相关区域⬆️️️️-----------

                            //-----------视频相关区域⬇️-----------
                            MainScope().launch {
                                binding.loadingStatText.text =
                                    "${binding.loadingStatText.text}\n视频正在加载中...马上就好！"


                            }

                            videoBvid?.let {
                                VideoManager.getVideoUrl(videoBvid, videoCid, object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        MainScope().launch {
                                            ToastUtils.makeText(
                                                "加载视频失败！"
                                            ).show()
                                            binding.loadingStatText.text =
                                                "${binding.loadingStatText.text}\n视频加载失败"


                                        }
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val responseString = response.body?.string()
                                        val videoUrls: VideoStreamsFlv = Gson().fromJson(
                                            responseString,
                                            VideoStreamsFlv::class.java
                                        )        //创建视频数据对象
                                        if (subtitleUrl != null) {
                                            MainScope().launch {
                                                binding.loadingStatText.text =
                                                    "${binding.loadingStatText.text}\n找到字幕，正在加载字幕"
                                            }
                                            NetworkUtils.getUrl(subtitleUrl, object : Callback {
                                                override fun onFailure(call: Call, e: IOException) {
                                                    MainScope().launch {
                                                        binding.loadingStatText.text =
                                                            "${binding.loadingStatText.text}\n字幕加载失败"
                                                    }
                                                }

                                                override fun onResponse(
                                                    call: Call,
                                                    response: Response
                                                ) {
                                                    val result = Gson().fromJson(
                                                        response.body?.string(),
                                                        Subtitle::class.java
                                                    )
                                                    MainScope().launch {
                                                        viewModel.subtitle = result
                                                        binding.loadingStatText.text =
                                                            "${binding.loadingStatText.text}\n字幕加载成功"
                                                        this@VideoPlayerActivity.runOnUiThread {
                                                            viewModel.loadVideo(videoUrls.data.durl[0].url)
                                                        }

                                                    }
                                                }

                                            })
                                        } else {
                                            MainScope().launch {
                                                this@VideoPlayerActivity.runOnUiThread {
                                                    viewModel.loadVideo(videoUrls.data.durl[0].url)
                                                }

                                            }
                                        }
                                    }

                                })
                            }

                            //-----------视频相关区域⬆️️️-----------
                        }
                    })
                    binding.danmakuView.prepare(danmukuParser, danmakuContext)      //准备弹幕
                    binding.danmakuView.enableDanmakuDrawingCache(true)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("currentPosition", viewModel.mediaPlayer.currentPosition)
    }

    override fun onResume() {
        super.onResume()
        binding.videoTitle.requestFocus()
    }


    //surfaceview适配视频大小
    /*private fun resizeVideo(width: Int, height: Int) {
        if (width == 0 || height == 0) return       //啥都没有适配个__?
        if (width < height) {     //宽小于高，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                binding.playerFrame.height * width / height,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
        }
        if (width == height) {       //宽等于高，随便适配一个
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.height * width / height,
                Gravity.CENTER
            )
        }
        if (width > height) {        //高小于宽，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.width * height / width,
                Gravity.CENTER
            )
        }
        binding.mainPanel.requestLayout()
        binding.mainPanel.invalidate()
        binding.loadingPanel.requestLayout()
        binding.loadingPanel.invalidate()
    }*/

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            /*
             * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
             */if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                BatteryN = intent.getIntExtra("level", 0) //目前电量
                BatteryV = intent.getIntExtra("voltage", 0) //电池电压
                BatteryT = intent.getIntExtra("temperature", 0).toDouble() //电池温度
                when (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus = "充电状态"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus = "放电状态"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus = "未充电"
                    BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus = "充满电"
                    BatteryManager.BATTERY_STATUS_UNKNOWN -> BatteryStatus = "未知道状态"
                }
                when (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> BatteryTemp = "未知错误"
                    BatteryManager.BATTERY_HEALTH_GOOD -> BatteryTemp = "状态良好"
                    BatteryManager.BATTERY_HEALTH_DEAD -> BatteryTemp = "电池没有电"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryTemp = "电池电压过高"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryTemp = "电池过热"
                }
                binding.watchStats?.text =
                    "电量$BatteryN% 温度${BatteryT * 0.1}°C ${TimeUtils.getCurrentTime()}"
            }
        }
    }

    //监听屏幕旋转
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.refreshResolution()
    }

    /**
     *  解压弹幕数据
     *  From CSDN
     */
    fun decompress(data: ByteArray?): ByteArray? {
        var output: ByteArray?
        val decompresser = Inflater(true)
        decompresser.reset()
        decompresser.setInput(data)
        val o = data?.size?.let { ByteArrayOutputStream(it) }
        try {
            val buf = ByteArray(1024)
            while (!decompresser.finished()) {
                val i = decompresser.inflate(buf)
                o?.write(buf, 0, i)
            }
            output = o?.toByteArray()
        } catch (e: Exception) {
            output = data
            e.printStackTrace()
        } finally {
            try {
                o?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        decompresser.end()
        return output
    }

    private fun uploadVideoViewingProgress(bvid: String, cid: Long) {
        if (UserManager.isLoggedIn()) {
            lifecycleScope.launch {
                while (true) {
                    VideoManager.uploadVideoViewingProgress(
                        bvid,
                        cid,
                        ((viewModel.mediaPlayer.currentPosition) / 1000).toInt()
                    )
                    delay(4000)
                }
            }
        } else return
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}