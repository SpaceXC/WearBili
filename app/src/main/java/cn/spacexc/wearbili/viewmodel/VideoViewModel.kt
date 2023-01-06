package cn.spacexc.wearbili.viewmodel

import androidx.compose.foundation.ScrollState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.SimplestUniversalDataClass
import cn.spacexc.wearbili.dataclass.subtitle.get.SubtitleInfo
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.dataclass.video.state.CoinState
import cn.spacexc.wearbili.dataclass.video.state.FavState
import cn.spacexc.wearbili.dataclass.video.state.LikeState
import cn.spacexc.wearbili.dataclass.video.state.result.LikeResult
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
    val scrollState = ScrollState(0)

    private val _videoInfo = MutableLiveData<VideoDetailInfo>()
    val videoInfo: LiveData<VideoDetailInfo> = _videoInfo

    private val _uploaderFans = MutableLiveData<UserFans>()
    val uploaderFans: LiveData<UserFans> = _uploaderFans

    private val _subtitle = MutableLiveData<SubtitleInfo>()
    val subtitle: LiveData<SubtitleInfo> = _subtitle

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

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

        })
    }

    fun getSubtitle(bvid: String) {
        VideoManager.getVideoById(bvid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), SubtitleInfo::class.java)
                MainScope().launch {
                    _subtitle.value = result
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

    fun getIsLiked(bvid: String) {
        VideoManager.isLiked(bvid, object : NetworkUtils.ResultCallback<LikeState> {
            override fun onSuccess(result: LikeState, code: Int) {
                MainScope().launch {
                    isLiked.value = result.data == 1
                }
            }

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

        })
    }

    fun getIsCoined(bvid: String) {
        if (!UserManager.isLoggedIn()) return
        VideoManager.isCoined(bvid, object : NetworkUtils.ResultCallback<CoinState> {
            override fun onSuccess(result: CoinState, code: Int) {
                MainScope().launch {
                    isCoined.value = result.data.multiply != 0
                }
            }

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

        })
    }

    fun getIsFavorite(bvid: String) {
        VideoManager.isFavorite(bvid, object : NetworkUtils.ResultCallback<FavState> {
            override fun onSuccess(result: FavState, code: Int) {
                MainScope().launch {
                    isFavorite.value = result.data.favoured
                }
            }

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }
        })
    }

    fun likeVideo(bvid: String, isLike: Boolean) {
        VideoManager.likeVideo(bvid, isLike, object : NetworkUtils.ResultCallback<LikeResult> {
            override fun onSuccess(result: LikeResult, code: Int) {
                if (code == 0) {
                    MainScope().launch {
                        isLiked.value = !(isLiked.value ?: false)
                        ToastUtils.showText(if (isLiked.value == true) "点赞成功" else "取消成功")
                        //isLiked.value = isLike

                    }
                }
            }

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
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