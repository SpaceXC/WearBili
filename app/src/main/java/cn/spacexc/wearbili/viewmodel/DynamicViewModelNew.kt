package cn.spacexc.wearbili.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DynamicItem
import cn.spacexc.wearbili.manager.DynamicManagerNew
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException

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

class DynamicViewModelNew : ViewModel() {
    val lazyListState = LazyListState()

    private val _dynamicItemList = MutableLiveData<List<DynamicItem>>()
    val dynamicItemList: LiveData<List<DynamicItem>> = _dynamicItemList

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val dynamicDetailItem = MutableLiveData<DynamicItem>()
    private val _commentList = MutableLiveData<List<CommentContentData>?>()
    val commentList: LiveData<List<CommentContentData>?> = _commentList

    private val _topComment = MutableLiveData<CommentContentData?>()
    val topComment: LiveData<CommentContentData?> = _topComment
    val commentCount = MutableLiveData(0)

    val isError = MutableLiveData(false)

    private val dynamicManager = DynamicManagerNew()

    fun getDynamic(isRefreshing: Boolean) {
        MainScope().launch {
            if (isRefreshing) _isRefreshing.value = true
        }
        dynamicManager.getRecommendDynamic(isRefreshing, onFailed = {
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

    fun getDynamicDetail(dyId: String) {
        dynamicManager.getDynamicDetail(dyId = dyId, onFailed = {
            MainScope().launch {
                if (it is IOException) {
                    ToastUtils.showText("网络异常")
                }
                isError.value = true
                _isRefreshing.value = false
            }
        }, onSuccess = {
            MainScope().launch {
                dynamicDetailItem.value = it.data.item
                _isRefreshing.value = false
            }
        })
    }

    fun getDynamicComments(isRefresh: Boolean, dyId: String, type: String) {
        val requestType = when (type) {
            "DYNAMIC_TYPE_FORWARD" -> 17
            "DYNAMIC_TYPE_DRAW" -> 11
            "DYNAMIC_TYPE_WORD" -> 17
            "DYNAMIC_TYPE_AV" -> 1
            else -> 0
        }
        dynamicManager.getDynamicComments(
            isRefresh = isRefresh,
            dyId = dyId,
            type = requestType,
            onFailed = {
                MainScope().launch {
                    if (it is IOException) {
                        ToastUtils.showText("网络异常")
                    }
                    isError.value = true
                    _isRefreshing.value = false
                }
            },
            onSuccess = {
                MainScope().launch {
                    _topComment.value = it.data.top?.upper
                    if (isRefresh) {
                        _commentList.value = it.data.replies
                        commentCount.value = it.data.cursor.all_count
                    } else {
                        _commentList.value =
                            _commentList.value?.plus(it.data.replies ?: emptyList())
                    }
                }
            })
    }
}