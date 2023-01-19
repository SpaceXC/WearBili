package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CommentRepliesViewModel : ViewModel() {
    var page = 1

    private val _rootComment = MutableLiveData<CommentContentData>()
    val rootComment: LiveData<CommentContentData> = _rootComment

    private val _commentReplies = MutableLiveData<List<CommentContentData>>()
    val commentReplies: LiveData<List<CommentContentData>> = _commentReplies

    val commentRepliesCount = MutableLiveData(0)

    val isLoading = MutableLiveData(false)
    val isError = MutableLiveData(false)

    fun getCommentReplies(aid: Long, rootCommentId: Long, isRefresh: Boolean) {
        isLoading.value = true
        page++
        if (isRefresh) page = 1
        VideoManager.getCommentReplies(aid, rootCommentId, page, onFailed = {
            MainScope().launch {
                isLoading.value = false
                ToastUtils.showText("网络异常")
                isError.value = true
            }
        }, onSuccess = {
            MainScope().launch {
                isLoading.value = false
                if (isRefresh) {
                    commentRepliesCount.value = it.data.page.count
                    _rootComment.value = it.data.root
                    _commentReplies.value = it.data.replies ?: emptyList()
                } else {
                    _commentReplies.value =
                        _commentReplies.value?.plus(it.data.replies ?: emptyList())
                }
            }
        })
    }
}