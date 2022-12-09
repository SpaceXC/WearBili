package cn.spacexc.wearbili.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    init {
        getAllDownloads()
    }

    private fun getAllDownloads() {
        Thread {
            while (true) {
                val result = ExoPlayerUtils.getInstance(cn.spacexc.wearbili.Application.context!!)
                    .getDownloadedVideos()
                MainScope().launch {
                    downloads.value = result
                }
                Thread.sleep(600)
            }
        }.start()
    }

    fun getDownload() {
        viewModelScope.launch {
            val result = ExoPlayerUtils.getInstance(cn.spacexc.wearbili.Application.context!!)
                .getDownloadedVideos()
            MainScope().launch {
                downloads.value = result
            }
        }
    }
}