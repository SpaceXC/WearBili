package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.spacevideo.UserSpaceVideo
import cn.spacexc.wearbili.dataclass.user.spacevideo.Vlist
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.LogUtils.log
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
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/10/6.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class UserSpaceViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _videos = MutableLiveData<List<Vlist>>()
    val videos: LiveData<List<Vlist>> = _videos
    var page = 1

    private val _dynamicCardList = MutableLiveData<List<Card>>()
    val dynamicCardList: LiveData<List<Card>> = _dynamicCardList

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    var videoPage = 1

    fun getUser(mid: Long) {
        UserManager.getUserById(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), User::class.java)
                MainScope().launch {
                    _user.value = result
                }
            }
        })
    }

    fun getVideos(mid: Long, isRefresh: Boolean) {
        ++page
        if (isRefresh) page = 1
        _isRefreshing.value = true
        UserManager.getUserSpaceVideo(
            mid,
            page,
            object : NetworkUtils.ResultCallback<UserSpaceVideo> {
                override fun onSuccess(result: UserSpaceVideo, code: Int) {
                    result.log()
                    MainScope().launch {
                        _isRefreshing.value = false
                        if (isRefresh) _videos.value =
                            result.data.list.vlist ?: emptyList() else _videos.value =
                            _videos.value?.plus(result.data.list.vlist ?: emptyList())
                    }
                }

                override fun onFailed(e: Exception) {
                    MainScope().launch {
                        _isRefreshing.value = false
                        ToastUtils.showText("网络异常")
                    }
                }

            })
    }

    fun getDynamic(mid: Long) {
        MainScope().launch { _isRefreshing.value = true }
        DynamicManager.getSpaceDynamics(mid, object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: Exception) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                    _isRefreshing.value = false
                }
            }

            override fun onSuccess(dynamicCards: List<Card>, code: Int) {
                MainScope().launch {
                    _dynamicCardList.value = dynamicCards
                    _isRefreshing.value = false
                }
            }

        })
    }

    fun getMoreDynamic(mid: Long) {
        MainScope().launch { _isRefreshing.value = true }
        DynamicManager.getMoreSpaceDynamic(mid, object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: Exception) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                    _isRefreshing.value = false
                }
            }

            override fun onSuccess(dynamicCards: List<Card>, code: Int) {
                MainScope().launch {
                    _dynamicCardList.value = _dynamicCardList.value?.plus(dynamicCards)
                    _isRefreshing.value = false
                }
            }

        })
    }
}