package cn.spacexc.wearbili.activity.video

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.manager.VideoManager
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MinifyVideoPlayer : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minify_video_player)
        videoView = findViewById(R.id.videoPlayer)
        progressBar = findViewById(R.id.progressBar)
        videoView.setMediaController(MediaController(this))
        loadVideo()
    }

    fun loadVideo() {
        val bvid = intent.getStringExtra("videoBvid")
        val cid = intent.getLongExtra("videoCid", 0)
        VideoManager.getVideoMp4Url(bvid!!, cid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loadVideo()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                MainScope().launch {
                    val videoUrls: VideoStreamsFlv = Gson().fromJson(
                        responseString,
                        VideoStreamsFlv::class.java
                    )        //创建视频数据对象
                    val headers = mapOf<String, String>(
                        "Referer" to "https://bilibili.com/",
                        "User-Agent" to "Mozilla/5.0 BiliDroid/*.*.* (bbcallen@gmail.com)"
                    )
                    videoView.setVideoURI(Uri.parse(videoUrls.data.durl[0].url), headers)
                    videoView.setOnPreparedListener {
                        progressBar.isVisible = false
                        videoView.start()
                    }
                    videoView.setOnErrorListener { mediaPlayer, i, i2 ->
                        Log.d(TAG, "onResponse: Error!")
                        true
                    }
                }

            }

        })
    }
}