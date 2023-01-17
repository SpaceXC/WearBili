package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.BaseData
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DynamicItem
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.dataclass.user.UserFans
import cn.spacexc.wearbili.dataclass.user.spacevideo.UserSpaceVideo
import cn.spacexc.wearbili.dataclass.user.spacevideo.Vlist
import cn.spacexc.wearbili.manager.DynamicManagerNew
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

    private val _videos = MutableLiveData<List<Vlist>?>()
    val videos: LiveData<List<Vlist>?> = _videos

    private val _dynamicItemList = MutableLiveData<List<DynamicItem>>()
    val dynamicItemList: LiveData<List<DynamicItem>> = _dynamicItemList

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean> = _isFollowed

    private val _fans = MutableLiveData<Int>()
    val fans: LiveData<Int> = _fans

    val isError = MutableLiveData(false)

    var videoPage = 1

    private val dynamicManager = DynamicManagerNew()

    fun getUser(mid: Long) {
        UserManager.getUserById(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    isError.value = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), User::class.java)
                MainScope().launch {
                    if (result.code == 0) {
                        _user.value = result
                    } else {
                        isError.value = true
                    }
                }
            }
        })
    }

    fun getVideos(mid: Long, isRefresh: Boolean, keyword: String = "") {
        ++videoPage
        if (isRefresh) videoPage = 1
        _isRefreshing.value = true
        UserManager.getUserSpaceVideo(
            mid,
            videoPage,
            keyword,
            object : NetworkUtils.ResultCallback<UserSpaceVideo> {
                override fun onSuccess(result: UserSpaceVideo, code: Int) {
                    result.log()
                    MainScope().launch {
                        _isRefreshing.value = false
                        if (result.code == 0) {
                            if (result.data.list.vlist.isNullOrEmpty()) {
                                if (isRefresh) {
                                    _videos.value = emptyList()
                                    return@launch
                                } else return@launch
                            }
                            if (isRefresh)
                                _videos.value = result.data.list.vlist
                            else _videos.value =
                                _videos.value?.plus(result.data.list.vlist)
                        } else {
                            isError.value = true
                        }
                    }
                }

                override fun onFailed(e: Exception?) {
                    MainScope().launch {
                        _isRefreshing.value = false
                        ToastUtils.showText("网络异常")
                        isError.value = true
                    }
                }

            })
    }

    fun getDynamic(isRefreshing: Boolean, mid: Long) {
        MainScope().launch {
            if (isRefreshing) _isRefreshing.value = true
        }
        dynamicManager.getSpaceDynamic(isRefreshing, mid = mid, onFailed = {
            MainScope().launch {
                if (it is IOException) {
                    ToastUtils.showText("网络异常")
                }
                isError.value = true
                _isRefreshing.value = false
            }
        }, onSuccess = {
            MainScope().launch {
                if (isRefreshing)
                    _dynamicItemList.value = it.data.items
                else
                    _dynamicItemList.value = _dynamicItemList.value?.plus(it.data.items)
                _isRefreshing.value = false
            }
        })
    }

    fun followUser(mid: Long) {
        UserManager.subscribeUser(mid, 11, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                    if (result.code == 0) {
                        MainScope().launch {
                            ToastUtils.showText("关注成功")
                            _isFollowed.value = true
                        }
                    }
                } catch (e: Exception) {
                    MainScope().launch {
                        ToastUtils.showText("关注失败")
                    }
                }
            }

        })
    }

    fun unfollowUser(mid: Long) {
        UserManager.deSubscribeUser(mid, 11, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                    if (result.code == 0) {
                        MainScope().launch {
                            ToastUtils.showText("取关成功")
                            _isFollowed.value = false
                        }
                    }
                } catch (e: Exception) {
                    MainScope().launch {
                        ToastUtils.showText("取关失败")
                    }
                }
            }

        })
    }

    fun getUserFans(mid: Long) {
        UserManager.getUserFans(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    _fans.value =
                        Gson().fromJson(
                            response.body?.string(),
                            UserFans::class.java
                        ).data.card.fans.toInt()
                }
            }
        })
    }

    fun checkSubscribe(mid: Long) {
        if (UserManager.isLoggedIn()) {
            UserManager.getUserById(mid, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.showText("网络异常")
                        isError.value = true
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val user = Gson().fromJson(response.body?.string(), User::class.java)
                    MainScope().launch {
                        if (user.code == 0) {
                            _isFollowed.value = user.data.is_followed
                        } else {
                            isError.value = true
                        }
                    }
                }

            })
        }
    }
}