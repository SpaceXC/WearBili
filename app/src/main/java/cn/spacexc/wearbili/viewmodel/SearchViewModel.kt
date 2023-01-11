package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.search.Search
import cn.spacexc.wearbili.dataclass.search.mediaft.SearchedMediaFt
import cn.spacexc.wearbili.dataclass.search.user.SearchedUser
import cn.spacexc.wearbili.dataclass.search.video.SearchedVideo
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/24.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SearchViewModel : ViewModel() {
    private val _searchedVideosData =
        MutableLiveData<List<cn.spacexc.wearbili.dataclass.search.Result>>()
    val searchedUser = MutableLiveData<List<SearchedUser>>()
    val searchedMediaFt = MutableLiveData<List<SearchedMediaFt>>()
    val searchedVideo = MutableLiveData<List<SearchedVideo>>()
    var page = 1

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun searchVideo(keyword: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            _isRefreshing.value = true
            page = 1
        }
        VideoManager.searchVideoAll(keyword, page, object : NetworkUtils.ResultCallback<Search> {
            override fun onSuccess(result: Search, code: Int) {
                MainScope().launch {
                    if (page == 1) {
                        val userList = result.data.result.find { it.resultType == "bili_user" }
                        val userTree = Gson().toJsonTree(userList?.data)
                        val userItems: List<SearchedUser> = Gson().fromJson(
                            userTree,
                            object : TypeToken<List<SearchedUser>>() {}.type
                        )
                        searchedUser.value = userItems

                        val mediaList = result.data.result.find { it.resultType == "media_ft" }
                        val mediaTree = Gson().toJsonTree(mediaList?.data)
                        val mediaItems: List<SearchedMediaFt> = Gson().fromJson(
                            mediaTree,
                            object : TypeToken<List<SearchedMediaFt>>() {}.type
                        )
                        searchedMediaFt.value = mediaItems
                    }
                    val videoList = result.data.result.find { it.resultType == "video" }
                    val videoTree = Gson().toJsonTree(videoList?.data)
                    val videoItems: List<SearchedVideo> = Gson().fromJson(
                        videoTree,
                        object : TypeToken<List<SearchedVideo>>() {}.type
                    )
                    if (isRefresh) {
                        _searchedVideosData.value = result.data.result
                        searchedVideo.value = videoItems
                    } else {
                        _searchedVideosData.value =
                            _searchedVideosData.value?.plus(result.data.result)
                        searchedVideo.value = searchedVideo.value?.plus(videoItems)
                    }
                    _isRefreshing.value = false
                    page++
                }
            }

            override fun onFailed(e: Exception?) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                    _isRefreshing.value = false
                }
            }

        })
    }
}