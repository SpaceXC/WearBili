package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.dataclass.bangumi.BangumiDetail
import cn.spacexc.wearbili.dataclass.bangumi.follow.follow.BangumiFollowed
import cn.spacexc.wearbili.dataclass.bangumi.follow.unfollow.BangumiUnfollowed
import cn.spacexc.wearbili.dataclass.bangumi.timeline.BangumiTimeLine
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

/*
 * Created by XC on 2022/10/28.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
const val ID_TYPE_SSID = "season_id"
const val ID_TYPE_EPID = "ep_id"

object BangumiManager {
    fun getBangumiDetail(
        id: String,
        idType: String = ID_TYPE_EPID,
        callback: NetworkUtils.ResultCallback<BangumiDetail>
    ) {
        NetworkUtils.getUrl(
            "http://api.bilibili.com/pgc/view/web/season?$idType=$id".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), BangumiDetail::class.java)
                    callback.onSuccess(result)
                }
            }
        )
    }

    fun getBangumiTimeLine(callback: NetworkUtils.ResultCallback<BangumiTimeLine>) {
        NetworkUtils.getUrl(
            "http://api.bilibili.com/pgc/web/timeline?types=1&before=1&after=1",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result =
                        Gson().fromJson(response.body?.string(), BangumiTimeLine::class.java)
                    callback.onSuccess(result)
                }

            }
        )
    }

    fun followBangumi(ssid: String, callback: NetworkUtils.ResultCallback<BangumiFollowed>) {
        val body = FormBody.Builder()
            .add("season_id", ssid)
            .add("csrf", CookiesManager.getCsrfToken() ?: "")
            .build()
        NetworkUtils.postUrl(
            "https://api.bilibili.com/pgc/web/follow/add",
            body,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string()
                    try {
                        val result = Gson().fromJson(str, BangumiFollowed::class.java)
                        if (result.code == 0) {
                            callback.onSuccess(result)
                        } else {
                            callback.onFailed(null)
                            MainScope().launch {
                                ToastUtils.showText("${result.code}: ${result.message}")
                                ToastUtils.debugToast(str.log() ?: "")
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        MainScope().launch {
                            ToastUtils.showText("请求错误")
                            ToastUtils.debugToast(str.log() ?: "")
                        }
                    }
                }
            })
    }

    fun unfollowBangumi(ssid: String, callback: NetworkUtils.ResultCallback<BangumiUnfollowed>) {
        val body = FormBody.Builder()
            .add("season_id", ssid)
            .add("csrf", CookiesManager.getCsrfToken() ?: "")
            .build()
        NetworkUtils.postUrl(
            "https://api.bilibili.com/pgc/web/follow/del",
            body,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string()
                    try {
                        val result = Gson().fromJson(str, BangumiUnfollowed::class.java)
                        if (result.code == 0) {
                            callback.onSuccess(result)
                        } else {
                            callback.onFailed(null)
                            MainScope().launch {
                                ToastUtils.showText("${result.code}: ${result.message}")
                                ToastUtils.debugToast(str.log() ?: "")
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        MainScope().launch {
                            ToastUtils.showText("请求错误")
                            ToastUtils.debugToast(str.log() ?: "")
                        }
                    }
                }
            })
    }
}