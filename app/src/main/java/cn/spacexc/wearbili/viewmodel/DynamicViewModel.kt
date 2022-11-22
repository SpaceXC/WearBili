package cn.spacexc.wearbili.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call

/* 
WearBili  Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/11/19.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DynamicViewModel : ViewModel() {
    val lazyListState = LazyListState()

    private val _dynamicCardList = MutableLiveData<List<Card>>()
    val dynamicCardList: LiveData<List<Card>> = _dynamicCardList

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun getDynamic() {
        MainScope().launch { _isRefreshing.value = true }
        DynamicManager.getRecommendDynamics(object : DynamicManager.DynamicResponseCallback {
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

    fun getMoreDynamic() {
        MainScope().launch { _isRefreshing.value = true }
        DynamicManager.getMoreDynamic(object : DynamicManager.DynamicResponseCallback {
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