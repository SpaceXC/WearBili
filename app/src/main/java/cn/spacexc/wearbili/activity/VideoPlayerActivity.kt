package cn.spacexc.wearbili.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.databinding.ActivityVideoPlayerBinding
import cn.spacexc.wearbili.dataclass.OnlineInfos
import cn.spacexc.wearbili.dataclass.VideoInfoData
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.viewmodel.PlayerStatus
import cn.spacexc.wearbili.viewmodel.VideoPlayerViewModel
import com.google.gson.Gson
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
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.Inflater

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding

    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    lateinit var viewModel: VideoPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val video: VideoInfoData? = intent.getParcelableExtra("videoData")
        lifecycleScope.launch {
            if (video != null) {
                VideoManager.getOnlineCount(video.bvid, video.cid, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call, response: Response) {
                        mThreadPool.execute {
                            this@VideoPlayerActivity.runOnUiThread {
                                val info = Gson().fromJson(
                                    response.body?.string(),
                                    OnlineInfos::class.java
                                )
                                binding.onlineCount.text = "${info.data.total}人在看"
                            }
                        }
                    }

                })
            }

            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }

        viewModel = ViewModelProvider(this)[VideoPlayerViewModel::class.java].apply {
            danmakuView = binding.danmakuView       //赋值viewmodel弹幕视图
            controllerBinding = binding.controllerInclude       //赋值viewmodel控制器视图

            //-----------LiveData监听区域⬇️-----------
            progressBarVisibility.observe(this@VideoPlayerActivity) {
                binding.progressBar.visibility = it
            }     //加载progress显示
            videoResolution.observe(this@VideoPlayerActivity) {
                binding.controllerInclude.progress.max = mediaPlayer.duration.toInt()
                binding.surfaceView.post {
                    resizeVideo(it.first, it.second)        //调整surfaceview大小
                }
            }
            bufferPercent.observe(this@VideoPlayerActivity) {
                binding.controllerInclude.progress.secondaryProgress =
                    binding.controllerInclude.progress.max * it / 100     //视频缓冲进度显示
            }
            //TODO("播放控制器显示/消失")
            controllerVisibility.observe(this@VideoPlayerActivity) {
                binding.controllerInclude.controllerFrame.visibility = it
            }
            //FIXME("在viewmodel监听播放状态，更改控制按钮图标")
            playerStat.observe(this@VideoPlayerActivity) {
                when (it) {
                    PlayerStatus.PLAYING -> {
                        binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                    PlayerStatus.PAUSED -> {
                        binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                    PlayerStatus.NOT_READY -> {
                        binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                    PlayerStatus.COMPLETED -> {
                        binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_replay_24)
                    }
                    else -> {}
                }
            }
            //-----------LiveData监听区域⬆️-----------

            //-----------Layout视图监听区域⬇️️-----------
            binding.surfaceView.holder.addCallback(object :
                SurfaceHolder.Callback {     //surfaceview监听
                override fun surfaceCreated(p0: SurfaceHolder) {}
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    viewModel.mediaPlayer.setVideoSurfaceHolder(holder)      //设置视频显示surfaceview
                    //playerViewModel.mediaPlayer.setScreenOnWhilePlaying(true)       //播放时不息屏//TODO
                }

                override fun surfaceDestroyed(p0: SurfaceHolder) {}
            })
            //TODO:点击屏幕触发播放控制器显示
            binding.playerFrame.setOnClickListener {
                viewModel.toggleControllerVisibility()
            }
            //TODO:播放控制按钮修改播放状态
            binding.controllerInclude.control.setOnClickListener {
                viewModel.togglePlayerStatus()
            }
            //TODO:快进3秒
            //TODO:快退3秒
            binding.controllerInclude.progress.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {     //进度条拖动监听
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        viewModel.playerSeekTo(progress)      //mediaplayer进度更改
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    viewModel.canDisappear = false        //拖动进度条时控制器不可消失
                    //playerViewModel.togglePlayerStatus()
                    viewModel.mediaPlayer.pause()     //拖动时暂停视频
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    viewModel.canDisappear = true     //松开进度条控制器可以消失
                    //playerViewModel.togglePlayerStatus()
                    viewModel.mediaPlayer.play()     //视频开始
                }
            })

            binding.pageName.setOnClickListener {
                finish()
            }
            //-----------Layout视图监听区域⬆️-----------

            //-----------弹幕相关区域⬇️️️-----------
            val danmakuContext: DanmakuContext = DanmakuContext.create()       //弹幕上下文

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

            if (video != null) {
                VideoManager.getDanmaku(video.cid, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        mThreadPool.execute {
                            this@VideoPlayerActivity.runOnUiThread {
                                Toast.makeText(
                                    this@VideoPlayerActivity,
                                    "加载弹幕失败！",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
                            mThreadPool.execute {
                                this@VideoPlayerActivity.runOnUiThread {
                                    Toast.makeText(
                                        this@VideoPlayerActivity,
                                        "加载弹幕失败！",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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

                                VideoManager.getVideoUrl(video.bvid, video.cid, object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        mThreadPool.execute {
                                            this@VideoPlayerActivity.runOnUiThread {
                                                Toast.makeText(
                                                    this@VideoPlayerActivity,
                                                    "加载视频失败！",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val responseString = response.body?.string()
                                        mThreadPool.execute {
                                            val videoUrls: VideoStreamsFlv = Gson().fromJson(
                                                responseString,
                                                VideoStreamsFlv::class.java
                                            )        //创建视频数据对象
                                            this@VideoPlayerActivity.runOnUiThread {
                                                viewModel.loadVideo(videoUrls.data.durl[0].url)

                                            }
                                        }
                                    }

                                })

                                //-----------视频相关区域⬆️️️-----------
                            }
                        })
                        binding.danmakuView.prepare(danmukuParser, danmakuContext)      //准备弹幕
                        binding.danmakuView.enableDanmakuDrawingCache(true)
                    }

                })
            }


        }
    }


    //surfaceview适配视频大小
    private fun resizeVideo(width: Int, height: Int) {
        if (width == 0 || height == 0) return
        if (width < height) {     //宽小于高，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                binding.playerFrame.height * width / height,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL
            )
        }
        if (width == height) {       //宽等于高，随便适配一个
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.height * width / height,
                Gravity.CENTER_HORIZONTAL
            )
        }
        if (width > height) {        //高小于宽，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.width * height / width,
                Gravity.CENTER_HORIZONTAL
            )
        }

    }

    /**
     *  解压弹幕数据
     *  From CSDN
     */
    fun decompress(data: ByteArray): ByteArray? {
        var output: ByteArray
        val decompresser = Inflater(true)
        decompresser.reset()
        decompresser.setInput(data)
        val o = ByteArrayOutputStream(data.size)
        try {
            val buf = ByteArray(1024)
            while (!decompresser.finished()) {
                val i = decompresser.inflate(buf)
                o.write(buf, 0, i)
            }
            output = o.toByteArray()
        } catch (e: Exception) {
            output = data
            e.printStackTrace()
        } finally {
            try {
                o.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        decompresser.end()
        return output
    }
}