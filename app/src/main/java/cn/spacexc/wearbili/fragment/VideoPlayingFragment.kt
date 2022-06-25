package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.databinding.FragmentVideoPlayingBinding
import cn.spacexc.wearbili.dataclass.VideoStreamUrls
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.viewmodel.PlayerStatus
import cn.spacexc.wearbili.viewmodel.VideoPlayerViewModel
import com.google.gson.Gson
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


class VideoPlayingFragment : Fragment() {
    private var _binding: FragmentVideoPlayingBinding? = null
    private val binding get() = _binding!!

    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    lateinit var playerViewModel: VideoPlayerViewModel


    init {
        Log.d(Application.getTag(), "VideoPlayingFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerViewModel = ViewModelProvider(this)[VideoPlayerViewModel::class.java].apply {     //获取Viewmodel
            danmakuView = binding.danmakuView
            controllerBinding = binding.controllerInclude
            progressBarVisibility.observe(viewLifecycleOwner) {
                binding.progressBar.visibility = it     //播放控制器加载显示
            }
            videoResolution.observe(viewLifecycleOwner) {
                binding.controllerInclude.progress.max = mediaPlayer.duration       //获取视频分辨率
                binding.surfaceView.post {
                    resizeVideo(
                        it.first,
                        it.second
                    )
                }        //调整surfaceview大小
            }
            controllerVisibility.observe(viewLifecycleOwner) {
                binding.controllerInclude.controllerFrame.visibility = it       //播放控制器显示
            }
            bufferPercent.observe(viewLifecycleOwner){
                binding.controllerInclude.progress.secondaryProgress = binding.controllerInclude.progress.max * it / 100        //视频缓冲进度显示
            }
            playerStat.observe(viewLifecycleOwner){
                binding.controllerInclude.control.isClickable = true        //监听播放状态
                toggleControlButton(it)     //更改控制按钮
            }
        }
        lifecycle.addObserver(playerViewModel.mediaPlayer)      //给mediaplayer添加生命周期
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback{     //surfaceview监听
            override fun surfaceCreated(p0: SurfaceHolder) {}
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                playerViewModel.mediaPlayer.setDisplay(holder)      //设置视频显示surfaceview
                playerViewModel.mediaPlayer.setScreenOnWhilePlaying(true)       //播放时不息屏
            }
            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })
        binding.playerFrame.setOnClickListener { playerViewModel.toggleControllerVisibility() }     //点击屏幕触发播放控制器显示
        binding.controllerInclude.control.setOnClickListener{ playerViewModel.togglePlayerStatus() }        //播放控制按钮修改播放状态
        binding.controllerInclude.fastForward.setOnClickListener{ playerViewModel.fastSeek(3) }     //快进3秒
        binding.controllerInclude.fastRewind.setOnClickListener{ playerViewModel.fastSeek(-3) }     //快退3秒
        binding.controllerInclude.progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{     //进度条拖动监听
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    playerViewModel.playerSeekTo(progress)      //mediaplayer进度更改
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                playerViewModel.canDisappear = false        //拖动进度条时控制器不可消失
                //playerViewModel.togglePlayerStatus()
                playerViewModel.mediaPlayer.pause()     //拖动时暂停视频
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                playerViewModel.canDisappear = true     //松开进度条控制器可以消失
                //playerViewModel.togglePlayerStatus()
                playerViewModel.mediaPlayer.start()     //视频开始
            }

        })
        //updatePlayerProgress()      //无限循环监听播放进度显示

        val danmakuContext : DanmakuContext = DanmakuContext.create()       //弹幕上下文

        val maxLinesPair = HashMap<Int, Int>()     //弹幕最多行数
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_LR] = 5

        val overlappingEnablePair = HashMap<Int, Boolean>()     //防弹幕重叠
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_LR] = true
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_BOTTOM] = true

        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 1F) //设置描边样式
            .setDuplicateMergingEnabled(true)      //合并重复弹幕
            .setScrollSpeedFactor(1.2f)     //弹幕速度
            .setScaleTextSize(0.6f)     //文字大小
            .setCacheStuffer(SpannedCacheStuffer()) // 图文混排使用SpannedCacheStuffer  设置缓存绘制填充器，默认使用{@link SimpleTextCacheStuffer}只支持纯文字显示, 如果需要图文混排请设置{@link SpannedCacheStuffer}如果需要定制其他样式请扩展{@link SimpleTextCacheStuffer}|{@link SpannedCacheStuffer}
            .setMaximumLines(maxLinesPair) //设置最大显示行数
            .preventOverlapping(overlappingEnablePair) //设置防弹幕重叠，null为允许重叠

        val loader: ILoader =
            DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)       //设置解析b站xml弹幕
        //loader.load(NetworkUtils.sendRequestWithHttpURLConnection(("http://api.bilibili.com/x/v1/dm/list.so?oid=${(activity as VideoActivity).currentVideo.cid}")).byteInputStream())
        if ((activity as VideoActivity).currentVideo == null) return
        VideoManager.getDanmaku(
            (activity as VideoActivity).currentVideo.cid,
            object : Callback {        //获取弹幕数据
            //网络请求失败
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute{
                    requireActivity().runOnUiThread{
                        Toast.makeText(requireContext(), "加载弹幕失败！", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            //网络请求成功
            override fun onResponse(call: Call, response: Response) {
                try {
                    //val responseIs = response.body?.byteStream()
                    //val str = String(decompress(responseIs.readBytes()).inputStream()!!)
                    //Log.d(Application.getTag(), "onResponse: ${response.body?.string()}")
                    val danmakuIs: ByteArray? = decompress(response.body?.bytes()!!)
                    loader.load(danmakuIs!!.inputStream())
                    Log.d(Application.getTag(), "onResponse: ${String(danmakuIs)}")
                }
                catch (e : IllegalDataException){
                    e.printStackTrace()
                    mThreadPool.execute{
                        requireActivity().runOnUiThread{
                            Toast.makeText(requireContext(), "加载弹幕失败！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                val danmukuParser = BiliDanmukuParser()
                val dataSource : IDataSource<*> = loader.dataSource
                danmukuParser.load(dataSource)

                //弹幕view回调
                binding.danmakuView.setCallback(object : DrawHandler.Callback{
                    //弹幕加载准备完成
                    override fun prepared() {
                        Log.d(Application.getTag(), "prepared: ")

                        VideoManager.getVideoUrl((activity as VideoActivity).currentVideo.bvid, (activity as VideoActivity).currentVideo.cid, object : Callback{                                //请求视频数据
                            //网络请求失败
                            override fun onFailure(call: Call, e: IOException) {
                                mThreadPool.execute{
                                    requireActivity().runOnUiThread{
                                        Toast.makeText(requireContext(), "加载视频失败！", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            //网络请求成功
                            override fun onResponse(call: Call, response: Response) {
                                val responseString = response.body?.string()
                                mThreadPool.execute{
                                    val videoUrls: VideoStreamUrls = Gson().fromJson(
                                        responseString,
                                        VideoStreamUrls::class.java
                                    )        //创建视频数据对象
                                    requireActivity().runOnUiThread {
                                        //TODO
                                        //playerViewModel.loadVideo("https://upos-sz-mirrorhw.bilivideo.com/upgcxcode/98/13/735201398/735201398-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1656124801&gen=playurlv2&os=hwbv&oi=1972173892&trid=d626ae5ea2134c94900f31d36063f731u&mid=0&platform=pc&upsig=d6b9abad1badf0064977a501a8988f28&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&agrr=0&bw=277413&logo=80000000")       //viewmodel加载视频
                                        playerViewModel.loadVideo(videoUrls.data.dash.video[0].baseUrl)       //viewmodel加载视频
                                        //playerViewModel.loadVideo("https://upos-sz-mirrorhw.bilivideo.com/upgcxcode/98/13/735201398/735201398-1-64.flv?e=ig8euxZM2rNcNbNVhbdVhwdlhbdghwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1656093171&gen=playurlv2&os=hwbv&oi=1972173843&trid=39c7c20fb4624b97948991a7c8f94d2du&mid=0&platform=pc&upsig=052fd3fc21794c0c0030ecc586206068&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&agrr=1&bw=202991&logo=80000000")       //viewmodel加载视频
                                        //playerViewModel.loadVideo("https://upos-sz-mirrorhw.bilivideo.com/upgcxcode/98/13/735201398/735201398-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1656092539&gen=playurlv2&os=hwbv&oi=1972173843&trid=0684327603cb49ef84df9bd3cc4549e9u&mid=0&platform=pc&upsig=c1c1fc6ff1ef73c5112c0d8075b006fe&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&agrr=1&bw=277413&logo=80000000")       //viewmodel加载视频
//                                        val uri : Uri = Uri.parse(videoUrls.data.durl[0].url)
//                                        val headers = HashMap<String, String>()
//                                        headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
//                                        headers["Referer"] = "https://bilibili.com/"
//                                        binding.videoView.setVideoURI(uri, headers)
//                                        val mediaController = MediaController(requireContext())
//                                        binding.videoView.setMediaController(mediaController)
//                                        binding.videoView.setOnPreparedListener {
//                                            Toast.makeText(requireContext(), "视频加载完毕", Toast.LENGTH_SHORT).show()
//                                        }
                                        //binding.videoView.start()
                                        //binding.danmakuView.start()
                                    }
                                }
                            }

                        })
                    }

                    override fun updateTimer(timer: DanmakuTimer?) {
                        Log.d(Application.getTag(), "updateTimer: ${timer?.currMillisecond}")
                    }

                    override fun drawingFinished() {
                        Log.d(Application.getTag(), "drawingFinished: ")
                    }

                })
                binding.danmakuView.prepare(danmukuParser, danmakuContext)      //准备弹幕
                binding.danmakuView.enableDanmakuDrawingCache(true)
            }
        })
    }

    //surfaceview适配视频大小
    private fun resizeVideo(width : Int, height : Int) {
        if(width == 0 || height == 0) return
        if(width < height){     //宽小于高，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                binding.playerFrame.height * width / height,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_HORIZONTAL
            )
        }
        if(width == height) {       //宽等于高，随便适配一个
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.height * width / height,
                Gravity.CENTER_HORIZONTAL
            )
        }
        if(width > height) {        //高小于宽，优先适配高度
            binding.surfaceView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                binding.playerFrame.width * height / width,
                Gravity.CENTER_HORIZONTAL
            )
        }

    }




    //更改视频播放状态
    private fun toggleControlButton(stat : PlayerStatus) {
        when(stat){
            PlayerStatus.PLAYING -> {
                binding.controllerInclude.control.isEnabled = true      //控制按钮可以按下
                playerViewModel.canDisappear = true     //控制器可以自动消失
                binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_pause_24)
            }
            PlayerStatus.PAUSED -> {
                binding.controllerInclude.control.isEnabled = true      //控制按钮可以按下
                playerViewModel.canDisappear = true     //控制器可以自动消失
                binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            PlayerStatus.COMPLETED -> {
                binding.controllerInclude.control.isEnabled = true      //控制按钮可以按下
                playerViewModel.canDisappear = false        //控制器不自动消失
                binding.controllerInclude.control.setImageResource(R.drawable.ic_baseline_replay_24)
                playerViewModel.startedPlaying = false      //播放结束
            }
            PlayerStatus.NOT_READY -> binding.controllerInclude.control.isEnabled =
                false       //视频未准备好，控制按钮不能按下
        }
    }

    /**
     *  解压弹幕数据
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