package cn.spacexc.wearbili.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.dataclass.VideoStreamUrlData
import cn.spacexc.wearbili.dataclass.VideoStreamsFlv
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class VideoCacheViewModel : ViewModel() {
    val urlInfo = MutableLiveData<VideoStreamUrlData>()

    fun downloadVideo(bvid: String, cid: Long) {
        VideoManager.getVideoUrl(bvid, cid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), VideoStreamsFlv::class.java)
                MainScope().launch {
                    urlInfo.value = result.data
                }
                val downloadRequest =
                    DownloadRequest.Builder(cid.toString(), Uri.parse(result.data.durl[0].url))
                        .build()
                DownloadService.sendAddDownload(
                    Application.context!!,
                    cn.spacexc.wearbili.service.DownloadService::class.java,
                    downloadRequest,
                    false
                )
            }

        })
    }
}