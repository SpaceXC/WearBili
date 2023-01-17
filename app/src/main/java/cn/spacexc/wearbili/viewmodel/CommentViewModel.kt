package cn.spacexc.wearbili.viewmodel

import androidx.compose.foundation.lazy.LazyListState
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
 * Created by XC on 2023/1/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CommentViewModel : ViewModel() {
    val scrollState = LazyListState()

    var page = 1

    private val _commentList = MutableLiveData<List<CommentContentData>?>()
    val commentList: LiveData<List<CommentContentData>?> = _commentList

    private val _topComment = MutableLiveData<CommentContentData?>()
    val topComment: LiveData<CommentContentData?> = _topComment

    var isError = MutableLiveData(false)

    fun appendFrontComment(comment: CommentContentData) {
        val list = listOf(comment)
        _commentList.value = _commentList.value?.plus(list)
    }

    fun getComment(aid: Long, isRefresh: Boolean) {
        //VideoManager.getCommentsByLikes()
        page++
        if (isRefresh) page = 1
        VideoManager.getCommentsByLikes(aid, page, onFailed = {
            MainScope().launch {
                ToastUtils.showText("网络异常")
                isError.value = true
            }
        }, onSuccess = {
            MainScope().launch {
                _topComment.value = it.data.top?.upper
                if (isRefresh) {
                    _commentList.value = it.data.replies
                } else {
                    _commentList.value = _commentList.value?.plus(it.data.replies ?: emptyList())
                }
            }
        })
    }
}