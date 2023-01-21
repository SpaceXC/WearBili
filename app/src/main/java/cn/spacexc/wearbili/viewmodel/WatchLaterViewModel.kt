package cn.spacexc.wearbili.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.BaseData
import cn.spacexc.wearbili.dataclass.watchlater.WatchLater
import cn.spacexc.wearbili.dataclass.watchlater.WatchLaterListObject
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2023/1/21.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class WatchLaterViewModel : ViewModel() {
    private val _watchLaterList = MutableLiveData<List<WatchLaterListObject>>()
    val watchLaterList: LiveData<List<WatchLaterListObject>> = _watchLaterList

    val isError = mutableStateOf(false)
    val isRefreshing = mutableStateOf(false)

    fun getWatchLater(isRefresh: Boolean) {
        if (isRefresh) isRefreshing.value = true
        if (UserManager.isLoggedIn()) {
            UserManager.getWatchLater(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.showText("网络异常")
                        isError.value = true
                        isRefreshing.value = false
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    MainScope().launch {
                        isRefreshing.value = false
                    }
                    try {
                        val result =
                            Gson().fromJson(response.body?.string(), WatchLater::class.java)
                        if (result.code == 0) {
                            MainScope().launch {
                                _watchLaterList.value = result.data.list
                            }
                        } else {
                            MainScope().launch {
                                ToastUtils.showText("${result.code}: ${result.message}")
                                isError.value = true
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        MainScope().launch {
                            ToastUtils.showText("数据处理失败")
                            isError.value = true
                        }
                    }
                }

            })
        }
    }

    fun deleteWatchLater(aid: Long) {
        UserManager.deleteVideoFromWatchLater(aid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText(
                        "网络异常"
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                MainScope().launch {
                    if (result.code == 0) {
                        ToastUtils.showText("删除成功")
                    } else {
                        ToastUtils.showText("删除失败")
                    }
                    getWatchLater(true)
                }
            }

        })
    }
}