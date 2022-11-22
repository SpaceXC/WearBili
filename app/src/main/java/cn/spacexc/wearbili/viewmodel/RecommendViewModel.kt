package cn.spacexc.wearbili.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.ScalingLazyListState
import cn.spacexc.wearbili.dataclass.video.rcmd.Item
import cn.spacexc.wearbili.dataclass.video.rcmd.RecommendVideo
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/*
 * Created by XC on 2022/11/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class RecommendViewModel : ViewModel() {
    val lazyListState = LazyListState()
    val scalingLazyListState = ScalingLazyListState()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    private val _videoList = MutableLiveData<List<Item>>()
    val videoList: LiveData<List<Item>> = _videoList

    fun getRecommendVideos(isRefresh: Boolean) {
        MainScope().launch {
            _isRefreshing.value = true
        }
        VideoManager.getRecommendVideo(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    _isRefreshing.value = false
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body?.string()
                try {
                    val videos = Gson().fromJson(str, RecommendVideo::class.java)
                    MainScope().launch {
                        if (videos.code == 0) {
                            if (isRefresh) _videoList.value = videos.data.items
                            else _videoList.value = _videoList.value?.plus(videos.data.items)
                            if (isRefresh) ToastUtils.makeText(
                                "小电视推荐了一批新内容"
                            ).show()
                            _isRefreshing.value = false
                        } else {
                            _isRefreshing.value = false
                            ToastUtils.makeText(
                                "${videos.code}: ${videos.message}"
                            ).show()
                        }
                    }
                } catch (_: Exception) {
                    MainScope().launch {
                        _isRefreshing.value = false
                        ToastUtils.makeText(
                            "加载失败"
                        ).show()
                    }

                }
            }
        })
    }
}