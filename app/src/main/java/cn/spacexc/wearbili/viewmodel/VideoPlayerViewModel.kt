package cn.spacexc.wearbili.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.databinding.LayoutVideoControllerBinding
import cn.spacexc.wearbili.utils.TimeUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import master.flame.danmaku.ui.widget.DanmakuView
import kotlin.collections.set

/**
 * Created by XC-Qan on 2022/6/18.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

enum class PlayerStatus {
    PLAYING,
    PAUSED,
    COMPLETED,
    NOT_READY
}

class VideoPlayerViewModel() : ViewModel() {
    private var httpDataSourceFactory = DefaultHttpDataSource.Factory()

    //    private val dataSourceFactory: DataSource.Factory = DataSource.Factory {
//        httpDataSourceFactory.setUserAgent("Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)")
//        val headers = HashMap<String, String>()
//        headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
//        headers["Referer"] = "https://bilibili.com/"
//        httpDataSourceFactory.setDefaultRequestProperties(headers)
//        val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
//
//        dataSource.setRequestProperty("User-Agent", "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)")
//        dataSource.setRequestProperty("Referer", "https://bilibili.com/")
//
//        dataSource
//    }
    var mediaPlayer: ExoPlayer

    init {
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
        httpDataSourceFactory.setUserAgent("Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)")
        val headers = HashMap<String, String>()
        headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
        headers["Referer"] = "https://bilibili.com/"
        val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
        dataSource.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
        )
        dataSource.setRequestProperty("Referer", "https://bilibili.com/")
        httpDataSourceFactory.setDefaultRequestProperties(headers)
            .setDefaultRequestProperties(headers)
        mediaPlayer = ExoPlayer.Builder(Application.getContext())
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(Application.getContext())
                    .setDataSourceFactory(httpDataSourceFactory)
            )
            .build()
    }

    private val _progressBarVisibility = MutableLiveData(View.VISIBLE)
    val progressBarVisibility: LiveData<Int> = _progressBarVisibility
    private val _videoResolution = MutableLiveData(Pair(0, 0))
    val videoResolution: LiveData<Pair<Int, Int>> = _videoResolution

    lateinit var controllerBinding: LayoutVideoControllerBinding

    @SuppressLint("StaticFieldLeak")
    lateinit var danmakuView: DanmakuView

    private val _controllerVisibility = MutableLiveData(View.INVISIBLE)
    val controllerVisibility: LiveData<Int> = _controllerVisibility
    private var controllerShowTime = 0L

    private val _bufferPercent = MutableLiveData(0)
    val bufferPercent: LiveData<Int> = _bufferPercent

    var canDisappear: Boolean = true

    private val _playerStatus = MutableLiveData(PlayerStatus.NOT_READY)
    val playerStat: LiveData<PlayerStatus> = _playerStatus

    var startedPlaying: Boolean = false

    private var isPlaying: Boolean = false

    fun loadVideo(videoUrl: String) {
//        /*val httpDataSourceFactory = DefaultHttpDataSource.Factory()
//        val dataSourceFactory: DataSource.Factory = DataSource.Factory {
//            val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
//            // Set a custom authentication request header.
//            dataSource.setRequestProperty("User-Agent", "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)")
//            dataSource.setRequestProperty("Referer", "https://bilibili.com/")
//            dataSource
//        }
//        mediaPlayer = ExoPlayer.Builder(Application.getContext())
//            .setMediaSourceFactory(
//                DefaultMediaSourceFactory(Application.getContext())
//                    .setDataSourceFactory(dataSourceFactory)
//            )
//            .build()*/
        mediaPlayer.apply {
            _playerStatus.value = PlayerStatus.NOT_READY
            val uri = Uri.parse(videoUrl)
            _progressBarVisibility.value = View.VISIBLE

            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            setMediaSource(mediaSource)
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        ExoPlayer.STATE_IDLE -> {

                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            _progressBarVisibility.value = View.VISIBLE
                        }
                        ExoPlayer.STATE_READY -> {
                            _videoResolution.value = Pair(videoSize.width, videoSize.height)
                            _bufferPercent.value = bufferedPercentage
                            _progressBarVisibility.value = View.INVISIBLE
                            play()
                            Log.d(
                                Application.getTag(),
                                "onPlaybackStateChanged: MediaPlayerCurrent : ${mediaPlayer.currentPosition}"
                            )
                            //danmakuView.seekTo(mediaPlayer.currentPosition)
                            danmakuView.start(mediaPlayer.currentPosition)
                            this@VideoPlayerViewModel.isPlaying = true
                            updatePlayerProgress()

                        }
                        ExoPlayer.STATE_ENDED -> {
                            _playerStatus.value = PlayerStatus.COMPLETED
                            danmakuView.pause()
                            this@VideoPlayerViewModel.isPlaying = false
                        }
                        else -> {

                        }
                    }
                    Log.d(Application.getTag(), "changed state to $playbackState")
                }
            })
            prepare()
        }
//        mediaPlayer.apply {
//            _playerStatus.value = PlayerStatus.NOT_READY
//            reset()
//            //val uri : Uri = Uri.parse("https://upos-sz-mirrorhw.bilivideo.com/upgcxcode/98/13/735201398/735201398-1-64.flv?e=ig8euxZM2rNcNbNVhbdVhwdlhbdghwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1656167028&gen=playurlv2&os=hwbv&oi=1972173892&trid=b900b756c4a041428f0a617bed710c3du&mid=0&platform=pc&upsig=7476139e3354234c15499c1d0cf73490&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&agrr=0&bw=202991&logo=80000000")
//            //val uri : Uri = Uri.parse("https://upos-sz-mirrorcos.bilivideo.com/upgcxcode/98/13/735201398/735201398-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1656167144&gen=playurlv2&os=cosbv&oi=1972173892&trid=d6f2b78146fb4f69993aa67bee296b23u&mid=0&platform=pc&upsig=78411aaf8f44dbba43d9cc510bdebf83&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&agrr=0&bw=12057&logo=80000000")
//            val uri : Uri = Uri.parse(videoUrl)
//            val headers = HashMap<String, String>()
//            headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
//            headers["Referer"] = "https://bilibili.com/"
//            setDataSource(Application.getContext(), uri, headers)
//
//
//            _progressBarVisibility.value = View.VISIBLE
//            mediaPlayer.setOnPreparedListener {
//                _progressBarVisibility.value = View.INVISIBLE
//                mediaPlayer.start()
//                danmakuView.start()
//                this@VideoPlayerViewModel.isPlaying = true
//                _playerStatus.value = PlayerStatus.PLAYING
//                updatePlayerProgress()
//            }
//            mediaPlayer.setOnVideoSizeChangedListener { _, width, height ->
//                _videoResolution.value  = Pair(width, height)
//            }
//            mediaPlayer.setOnBufferingUpdateListener{ _, percent ->
//                _bufferPercent.value = percent
//            }
//            setOnCompletionListener {
//                _playerStatus.value = PlayerStatus.COMPLETED
//                danmakuView.pause()
//                this@VideoPlayerViewModel.isPlaying = false
//            }
//            setOnSeekCompleteListener {
//                _progressBarVisibility.value = View.INVISIBLE
//                danmakuView.seekTo(mediaPlayer.currentPosition.toLong())
//                //mediaPlayer.start()
//                danmakuView.resume()
//                togglePlayerStatus()
//                this@VideoPlayerViewModel.isPlaying = true
//            }
//            prepareAsync()
//        }
    }

    fun toggleControllerVisibility() {

        if(_controllerVisibility.value == View.INVISIBLE){
            _controllerVisibility.value = View.VISIBLE
            controllerShowTime = System.currentTimeMillis()
            viewModelScope.launch {
                delay(3000)
                if (System.currentTimeMillis() - controllerShowTime > 3000 && canDisappear) {
                    _controllerVisibility.value = View.INVISIBLE
                }
            }
        } else _controllerVisibility.value = View.INVISIBLE
    }

    //更新视频播放进度显示
    @SuppressLint("SetTextI18n")
    private fun updatePlayerProgress() {
        viewModelScope.launch {
            while (isPlaying) {
                delay(500)
                controllerBinding.progress.progress =
                    mediaPlayer.currentPosition.toInt()       //seekbar进度显示
                controllerBinding.currentPositionText.text =
                    "${TimeUtils.secondToTime((mediaPlayer.currentPosition / 1000))}/${
                        TimeUtils.secondToTime((mediaPlayer.duration / 1000))
                    }"       //文字进度显示
            }
        }
    }

    fun togglePlayerStatus() {
        when (_playerStatus.value) {
            PlayerStatus.PLAYING -> {
                mediaPlayer.pause()
                danmakuView.pause()
                _playerStatus.value = PlayerStatus.PAUSED
            }
            PlayerStatus.PAUSED -> {
                mediaPlayer.play()
                danmakuView.start(mediaPlayer.currentPosition)
                _playerStatus.value = PlayerStatus.PLAYING
            }
            PlayerStatus.COMPLETED -> {
                mediaPlayer.play()
                danmakuView.restart()
                _playerStatus.value = PlayerStatus.PLAYING
            }
            else -> return
        }
    }

    fun playerSeekTo(position : Int) {
        //_progressBarVisibility.value = View.VISIBLE
        danmakuView.pause()
        mediaPlayer.seekTo(position.toLong())
        isPlaying = false
    }

    fun fastSeek(second : Int){
        val ms = second * 1000
        val position = mediaPlayer.currentPosition + ms
        mediaPlayer.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        danmakuView.release()
    }
}