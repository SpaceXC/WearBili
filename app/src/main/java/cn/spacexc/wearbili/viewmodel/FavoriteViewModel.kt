package cn.spacexc.wearbili.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.star.StarList
import cn.spacexc.wearbili.dataclass.star.StarListObj
import cn.spacexc.wearbili.dataclass.star.result.FavoriteResult
import cn.spacexc.wearbili.manager.UserManager
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
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class FavoriteViewModel : ViewModel() {
    private val _favList = MutableLiveData<List<StarListObj>>()
    val favList: LiveData<List<StarListObj>> = _favList

    fun getFavList(aid: Long) {
        UserManager.getStarFolderList(aid = aid, callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), StarList::class.java)
                MainScope().launch {
                    _favList.value = result.data.list
                }
            }
        })
    }

    fun commitFav(
        aid: Long,
        idsToAdd: List<Long>,
        idsToDelete: List<Long>,
        callback: (Boolean) -> Unit
    ) {
        Log.d(TAG, "commitFav: $idsToAdd, $idsToDelete")
        UserManager.addOrRemoveVideoToFavorite(
            aid,
            idsToAdd,
            idsToDelete,
            object : NetworkUtils.ResultCallback<FavoriteResult> {
                override fun onSuccess(result: FavoriteResult, code: Int) {
                    callback(true)
                }

                override fun onFailed(e: Exception?) {
                    callback(false)
                    MainScope().launch {
                        ToastUtils.showText("网络异常")
                    }
                }

            })
    }
}