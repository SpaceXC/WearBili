package cn.spacexc.wearbili.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.spacexc.wearbili.dataclass.OnlineInfos
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.dataclass.video.parts.VideoPartItem
import cn.spacexc.wearbili.dataclass.video.parts.VideoParts
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ExoPlayerUtils
import cn.spacexc.wearbili.utils.LogUtils.log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.Inflater

/**
 * Created by XC-Qan on 2023/1/21.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class VideoPlayerViewModelV2(application: Application) : AndroidViewModel(application) {
    var videoPlayer: ExoPlayer  //exoplayer播放器对象
    private var httpDataSourceFactory: DefaultHttpDataSource.Factory =
        DefaultHttpDataSource.Factory()    //在线视频播放数据源
    private var cacheDataSourceFactory: DataSource.Factory      //缓存视频播放数据源

    val videoResolution = mutableStateOf(Pair(1, 1))

    val isError = mutableStateOf(false)

    val loadingStateText = mutableStateOf("")

    val isLoading = mutableStateOf(true)

    val videoPartsList = mutableStateOf(listOf<VideoPartItem>())

    val isControlPanelVisible = mutableStateOf(false)

    val videoProgress = mutableStateOf(0L)

    val isPlaying = mutableStateOf(false)

    val playerStat = mutableStateOf(ExoPlayer.STATE_IDLE)

    val onlineCount = mutableStateOf("-")

    val isDanmakuPlaying = MutableLiveData(false)
    val danmakuPosition = MutableLiveData(0L)

    init {
        httpDataSourceFactory.setUserAgent("Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)")      //必须要设置
        val headers = HashMap<String, String>()
        headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
        headers["Referer"] = "https://bilibili.com/"
        val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()   //在线视频播放数据源
        cacheDataSourceFactory = CacheDataSource.Factory()  //缓存数据源
            .setCache(
                ExoPlayerUtils.getInstance(cn.spacexc.wearbili.Application.context!!).getCache()
            )
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setCacheWriteDataSinkFactory(null) // Disable writing.

        dataSource.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
        )
        dataSource.setRequestProperty("Referer", "https://bilibili.com/")    //多设置几次（我也不知道为什么
        httpDataSourceFactory.setDefaultRequestProperties(headers)
            .setDefaultRequestProperties(headers)
        videoPlayer = ExoPlayer.Builder(application)
            .build()    //创建对象
    }

    /**
     * 加载并播放在线视频
     * @param url 视频url，需要带https协议头
     */
    fun loadHttpVideo(url: String) {
        videoPlayer.apply {
            val uri = Uri.parse(url)
            val mediaSource =
                ProgressiveMediaSource.Factory(httpDataSourceFactory).createMediaSource(
                    MediaItem.fromUri(uri)
                )
            setMediaSource(mediaSource) //设置媒体源
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    this@VideoPlayerViewModelV2.isPlaying.value = isPlaying
                    isDanmakuPlaying.value = isPlaying
                    if (isPlaying) updateVideoProgress()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    playerStat.value = playbackState
                    when (playbackState) {
                        ExoPlayer.STATE_READY -> {
                            videoResolution.value =
                                Pair(
                                    videoSize.width.log("width"),
                                    videoSize.height.log("height")
                                )     //videoPlayer.videoSize
                            appendLoadingState("完成！")
                            danmakuPosition.value = currentPosition
                            this@VideoPlayerViewModelV2.isLoading.value = false
                            play()
                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            appendLoadingState("缓冲中......")
                            this@VideoPlayerViewModelV2.isLoading.value = true
                        }
                        ExoPlayer.STATE_ENDED -> {
                            videoProgress.value = videoPlayer.currentPosition
                            isControlPanelVisible.value = true
                        }
                        else -> {}
                    }
                }
            })
            prepare()
        }
    }

    fun togglePlayerPlayingState() {
        videoPlayer.apply {
            if (playerStat.value == ExoPlayer.STATE_ENDED) {
                seekTo(0)
                play()
            } else if (playerStat.value == ExoPlayer.STATE_IDLE) {
                return
            } else {
                if (this@VideoPlayerViewModelV2.isPlaying.value) pause() else play()
            }
        }
    }

    fun toggleControlPanelVisibility() {
        viewModelScope.launch {
            isControlPanelVisible.value = !isControlPanelVisible.value
            if (isControlPanelVisible.value) {
                delay(5000)
                if (playerStat.value != ExoPlayer.STATE_ENDED) {
                    isControlPanelVisible.value = false
                }
            }
        }
    }

    fun updateVideoProgress() {
        viewModelScope.launch {
            while (isPlaying.value) {
                videoProgress.value = videoPlayer.currentPosition
                delay(1000)
            }
        }
    }

    fun getHttpVideoUrl(bvid: String, cid: Long) {
        appendLoadingState("正在获取视频链接")
        VideoManager.getVideoUrl(bvid, cid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    appendLoadingState("获取视频链接失败！")
                    isError.value = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result =
                        Gson().fromJson(response.body?.string(), VideoStreamsFlv::class.java)
                    if (result.code == 0) {
                        MainScope().launch {
                            loadHttpVideo(result.data.durl[0].url)
                            appendLoadingState("获取视频链接成功，正在加载视频......")
                        }
                    } else {
                        MainScope().launch {
                            appendLoadingState("获取视频链接失败！")
                            isError.value = true
                        }
                    }
                } catch (e: JsonSyntaxException) {
                    MainScope().launch {
                        appendLoadingState("获取视频链接失败！")
                        isError.value = true
                    }
                }
            }

        })
    }

    fun getVideoPartsList(bvid: String) {
        appendLoadingState("正在获取视频分P列表")
        VideoManager.getVideoParts(bvid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    appendLoadingState("获取视频分P失败！")
                    isError.value = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = Gson().fromJson(response.body?.string(), VideoParts::class.java)
                    if (result.code == 0) {
                        MainScope().launch {
                            videoPartsList.value = result.data
                            appendLoadingState("获取视频分P列表成功")
                        }
                    } else {
                        MainScope().launch {
                            appendLoadingState("获取视频分P失败！")
                            isError.value = true
                        }
                    }
                } catch (e: JsonSyntaxException) {
                    MainScope().launch {
                        appendLoadingState("获取视频分P失败！")
                        isError.value = true
                    }
                }
            }

        })
    }

    fun updateVideoPlayingProgress(bvid: String, cid: Long) {
        VideoManager.uploadVideoViewingProgress(
            bvid,
            cid,
            videoPlayer.currentPosition.div(1000).toInt()
        )
    }

    fun uploadVideoProgress(bvid: String, cid: Long) {
        if (UserManager.isLoggedIn()) {
            viewModelScope.launch {
                while (true) {
                    if (videoPlayer.playbackState != ExoPlayer.STATE_IDLE && videoPlayer.playbackState != ExoPlayer.STATE_ENDED) {
                        updateVideoPlayingProgress(bvid, cid)
                        delay(4000)
                    }
                }
            }
        }
    }

    fun getOnlineCount(bvid: String, cid: Long) {
        VideoManager.getOnlineCount(bvid, cid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    try {
                        val info = Gson().fromJson(
                            response.body?.string(),
                            OnlineInfos::class.java
                        )
                        if (info.code == 0) {
                            MainScope().launch {
                                onlineCount.value = info.data?.count ?: "-"
                            }
                        } else {
                            MainScope().launch {
                                appendLoadingState("获取在线观看人数失败")
                            }
                        }
                    } catch (e: Exception) {
                        MainScope().launch {
                            appendLoadingState("获取在线观看人数失败")
                        }
                    }
                }
            }

        })
    }

    fun getVideoDanmaku(cid: Long, onSuccess: (InputStream) -> Unit) {
        VideoManager.getDanmaku(cid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    isError.value = true
                    appendLoadingState("获取弹幕失败")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    decompress(response.body?.bytes())?.inputStream()?.let { onSuccess(it) }
                } catch (e: Exception) {
                    MainScope().launch {
                        isError.value = true
                        appendLoadingState("获取弹幕失败")
                    }
                }
            }

        })
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

    fun appendLoadingState(state: String) {
        loadingStateText.value = loadingStateText.value.plus("\n${state}")
    }
}