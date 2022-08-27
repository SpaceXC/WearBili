package cn.spacexc.wearbili.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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
    NOT_READY,
    Seeking
}

@SuppressLint("StaticFieldLeak")
class VideoPlayerViewModel : ViewModel() {
    private var httpDataSourceFactory = DefaultHttpDataSource.Factory()

    var mediaPlayer: ExoPlayer

    var progress = 0L

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
            .build()
    }

    private val _isLoadingPanelVisible = MutableLiveData(true)
    val isLoadingPanelVisible: LiveData<Boolean> = _isLoadingPanelVisible

    private val _videoResolution = MutableLiveData(Pair(0, 0))
    val videoResolution: LiveData<Pair<Int, Int>> = _videoResolution

    private val _isVideoLoaded = MutableLiveData(false)
    val isVideoLoaded: LiveData<Boolean> = _isVideoLoaded


    @SuppressLint("StaticFieldLeak")
    lateinit var danmakuView: DanmakuView

    @SuppressLint("StaticFieldLeak")
    lateinit var seekBar: SeekBar

    @SuppressLint("StaticFieldLeak")
    lateinit var progressText: TextView
    lateinit var statTextView: TextView


    private val _controllerVisibility = MutableLiveData(View.INVISIBLE)
    val controllerVisibility: LiveData<Int> = _controllerVisibility
    private var controllerShowTime = 0L

    private val _bufferPercent = MutableLiveData(0)
    val bufferPercent: LiveData<Int> = _bufferPercent

    var canDisappear: Boolean = true

    private val _playerStatus = MutableLiveData(PlayerStatus.NOT_READY)
    val playerStat: LiveData<PlayerStatus> = _playerStatus


    var isPlaying: Boolean = false

    lateinit var onSeekCompleteListener: OnSeekCompleteListener

    fun loadVideo(videoUrl: String) {
        mediaPlayer.apply {
            _playerStatus.value = PlayerStatus.NOT_READY
            val uri = Uri.parse(videoUrl)

            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            setMediaSource(mediaSource)
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        ExoPlayer.STATE_IDLE -> {
                            //_playerStatus.value = PlayerStatus.NOT_READY
                            //_progressBarVisibility.value = View.VISIBLE
                        }

                        ExoPlayer.STATE_BUFFERING -> {
                            _isLoadingPanelVisible.value = true
                            danmakuView.pause()
                            statTextView.text = "${statTextView.text}\n缓冲中"
                            this@VideoPlayerViewModel.isPlaying = false

                        }

                        ExoPlayer.STATE_READY -> {
                            _videoResolution.value = Pair(videoSize.width, videoSize.height)
                            _bufferPercent.value = bufferedPercentage
                            play()
                            danmakuView.start(mediaPlayer.currentPosition)
                            _isLoadingPanelVisible.value = false
                            _playerStatus.value = PlayerStatus.PLAYING
                            statTextView.text = "${statTextView.text}\n加载完成"
                            if (_isVideoLoaded.value == false && progress != 0L) {
                                mediaPlayer.seekTo(progress)
                                ToastUtils.showText("已为您定位到${(progress / 1000).secondToTime()}")
                            }
                            _isVideoLoaded.value = true
                            this@VideoPlayerViewModel.isPlaying = true
                            Log.d(
                                Application.getTag(),
                                "onPlaybackStateChanged: MediaPlayerCurrent : ${mediaPlayer.currentPosition}"
                            )
                            //danmakuView.seekTo(mediaPlayer.currentPosition)
                            onSeekCompleteListener.onSeek(mediaPlayer.currentPosition)
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
    }

    fun toggleControllerVisibility() {
        if (_controllerVisibility.value == View.INVISIBLE) {
            _controllerVisibility.postValue(View.VISIBLE)
            controllerShowTime = System.currentTimeMillis()
            viewModelScope.launch {
                delay(3000)
                if (System.currentTimeMillis() - controllerShowTime > 3000 && canDisappear) {
                    _controllerVisibility.postValue(View.INVISIBLE)
                }
            }
        } else _controllerVisibility.postValue(View.INVISIBLE)
    }

    //更新视频播放进度显示
    @SuppressLint("SetTextI18n")
    private fun updatePlayerProgress() {
        viewModelScope.launch {
            while (_playerStatus.value == PlayerStatus.PLAYING) {
                delay(500)
                seekBar.progress =
                    mediaPlayer.currentPosition.toInt()       //seekbar进度显示
                progressText.text =
                    "${(mediaPlayer.currentPosition / 1000).secondToTime()}/${
                        (mediaPlayer.duration / 1000).secondToTime()
                    }"       //文字进度显示
            }
        }

    }

    fun togglePlayerStatus() {
        Log.d(TAG, "togglePlayerStatus: ${_playerStatus.value}")
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

    fun refreshResolution() {
        _videoResolution.value = Pair(mediaPlayer.videoSize.width, mediaPlayer.videoSize.height)
    }

    fun playerSeekTo(position: Int) {
        danmakuView.pause()
        mediaPlayer.seekTo(position.toLong())
        isPlaying = false
    }

    fun playerSeekTo(position: Long) {
        danmakuView.pause()
        mediaPlayer.seekTo(position)
        isPlaying = false
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        danmakuView.release()
    }
}

interface OnSeekCompleteListener {
    fun onSeek(progress: Long)
}