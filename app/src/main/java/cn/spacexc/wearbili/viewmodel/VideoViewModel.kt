package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.SimplestUniversalDataClass
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.dataclass.videoDetail.VideoDetailInfo
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/*
 * Created by XC on 2022/10/31.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class VideoViewModel : ViewModel() {
    private val _videoInfo = MutableLiveData<VideoDetailInfo>()
    val videoInfo: LiveData<VideoDetailInfo> = _videoInfo

    private val _uploaderFans = MutableLiveData<UserFans>()
    val uploaderFans: LiveData<UserFans> = _uploaderFans

    private val _uploaderInfo = MutableLiveData<User>()
    val uploaderInfo: LiveData<User> = _uploaderInfo

    val isLiked = MutableLiveData(false)
    val isCoined = MutableLiveData(false)
    val isFavorite = MutableLiveData(false)

    fun getVideoInfo(bvid: String) {
        VideoManager.getVideoInfo(bvid, object : NetworkUtils.ResultCallback<VideoDetailInfo> {
            override fun onSuccess(result: VideoDetailInfo, code: Int) {
                MainScope().launch {
                    _videoInfo.value = result
                }
            }

            override fun onFailed(e: Exception) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

        })
    }

    fun getUploaderInfo(mid: Long) {
        UserManager.getUserById(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    _uploaderInfo.value =
                        Gson().fromJson(response.body?.string(), User::class.java)
                }
            }

        })
    }

    fun getFans(mid: Long) {
        UserManager.getUserFans(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    _uploaderFans.value =
                        Gson().fromJson(response.body?.string(), UserFans::class.java)
                }
            }

        })
    }

    fun addToViewLater(bvid: String) {
        VideoManager.addToView(bvid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {


                    ToastUtils.makeText(
                        "网络异常"
                    ).show()

                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(
                    response.body?.string(),
                    SimplestUniversalDataClass::class.java
                )
                MainScope().launch {
                    when (result.code) {
                        0 -> {
                            ToastUtils.makeText(
                                "添加成功"
                            ).show()
                        }
                        90001 -> {
                            ToastUtils.makeText(
                                "稍后再看列表已满"
                            ).show()
                        }
                        90003 -> {
                            ToastUtils.makeText(
                                "视频不见了"
                            ).show()
                        }

                    }
                }
            }

        })
    }
}