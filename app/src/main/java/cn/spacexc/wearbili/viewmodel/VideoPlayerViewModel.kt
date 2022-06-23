package cn.spacexc.wearbili.viewmodel

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.utils.XCMediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

class VideoPlayerViewModel : ViewModel() {
    val mediaPlayer = XCMediaPlayer()
    private val _progressBarVisibility = MutableLiveData(View.VISIBLE)
    val progressBarVisibility :LiveData<Int> = _progressBarVisibility
    private val _videoResolution = MutableLiveData(Pair(0, 0 ))
    val videoResolution : LiveData<Pair<Int, Int>> = _videoResolution

    private val _controllerVisibility = MutableLiveData(View.INVISIBLE)
    val controllerVisibility : LiveData<Int> = _controllerVisibility
    private var controllerShowTime = 0L

    private val _bufferPercent = MutableLiveData(0)
    val bufferPercent : LiveData<Int> = _bufferPercent

    var canDisappear : Boolean = true

    private val _playerStatus = MutableLiveData(PlayerStatus.NOT_READY)
    val playerStat : LiveData<PlayerStatus> = _playerStatus

    var startedPlaying : Boolean = false

    fun loadVideo(videoUrl : String) {
        mediaPlayer.apply {
            _playerStatus.value = PlayerStatus.NOT_READY
            reset()
            val uri : Uri = Uri.parse(videoUrl)
            val headers = HashMap<String, String>()
            headers["User-Agent"] = "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
            headers["Referer"] = "https://bilibili.com/"
            setDataSource(Application.getContext(), uri, headers)
            _progressBarVisibility.value = View.VISIBLE
            mediaPlayer.setOnPreparedListener {
                _progressBarVisibility.value = View.INVISIBLE
                mediaPlayer.start()
                _playerStatus.value = PlayerStatus.PLAYING
            }
            mediaPlayer.setOnVideoSizeChangedListener { _, width, height ->
                _videoResolution.value  = Pair(width, height)
            }
            mediaPlayer.setOnBufferingUpdateListener{ _, percent ->
                _bufferPercent.value = percent
            }
            setOnCompletionListener {
                _playerStatus.value = PlayerStatus.COMPLETED
            }
            setOnSeekCompleteListener {
                _progressBarVisibility.value = View.INVISIBLE
            }
            prepareAsync()
        }
    }

    fun toggleControllerVisibility() {

        if(_controllerVisibility.value == View.INVISIBLE){
            _controllerVisibility.value = View.VISIBLE
            controllerShowTime = System.currentTimeMillis()
            viewModelScope.launch {
                delay(3000)
                if(System.currentTimeMillis() - controllerShowTime > 3000 && canDisappear){
                    _controllerVisibility.value = View.INVISIBLE
                }
            }
        }
        else _controllerVisibility.value = View.INVISIBLE
    }

    fun togglePlayerStatus() {
        when (_playerStatus.value) {
            PlayerStatus.PLAYING -> {
                mediaPlayer.pause()
                _playerStatus.value = PlayerStatus.PAUSED
            }
            PlayerStatus.PAUSED -> {
                mediaPlayer.start()
                _playerStatus.value = PlayerStatus.PLAYING
            }
            PlayerStatus.COMPLETED -> {
                mediaPlayer.start()
                _playerStatus.value = PlayerStatus.PLAYING
            }
            else -> return
        }
    }

    fun playerSeekTo(position : Int) {
        _progressBarVisibility.value = View.VISIBLE
        mediaPlayer.seekTo(position)
    }

    fun fastSeek(second : Int){
        val ms = second * 1000
        val position = mediaPlayer.currentPosition + ms
        mediaPlayer.seekTo(position)
    }



    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}