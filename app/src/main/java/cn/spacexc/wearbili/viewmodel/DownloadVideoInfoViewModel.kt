package cn.spacexc.wearbili.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cn.spacexc.wearbili.utils.ExoPlayerUtils
import com.google.android.exoplayer2.offline.Download
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * Created by XC-Qan on 2022/9/22.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DownloadVideoInfoViewModel(application: Application) : AndroidViewModel(application) {
    val downloads = MutableLiveData<List<Download>>()
    val downloadings = MutableLiveData<List<Download>>()

    init {
        getDownloads()
    }


    private fun getDownloads() {
        Thread {
            while (true) {
                val downloadsList =
                    ExoPlayerUtils.getInstance(cn.spacexc.wearbili.Application.context!!)
                        .getDownloadedVideos()
                val downloadingsList =
                    ExoPlayerUtils.getInstance(cn.spacexc.wearbili.Application.context!!)
                        .getDownloadingVideos()
                MainScope().launch {
                    downloads.value = downloadsList
                    downloadings.value = downloadingsList

                }
                Thread.sleep(600)
            }
        }.start()
    }
}