package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.dataclass.VideoComment
import cn.spacexc.wearbili.dataclass.dynamic.new.detail.DynamicDetail
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DynamicList
import cn.spacexc.wearbili.exception.IllegalDataReturnException
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import cn.spacexc.wearbili.utils.USER_AGENT
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DynamicManagerNew {
    var recommendDynamicOffset = ""
    var recommendDynamicPage = 1

    var spaceDynamicOffset = ""
    var spaceDynamicPage = 1

    var commentPage = 1

    fun getRecommendDynamic(
        isRefresh: Boolean,
        onFailed: (Exception) -> Unit,
        onSuccess: (DynamicList) -> Unit
    ) {
        if (isRefresh) recommendDynamicPage = 1
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/all?timezone_offset=-480&type=all${if (recommendDynamicOffset.isEmpty()) "" else "&offset=$recommendDynamicOffset"}&page=$recommendDynamicPage".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body?.string()?.debugToast().log()
                    try {
                        val result = Gson().fromJson(responseStr, DynamicList::class.java)
                        if (result.code == 0) {
                            onSuccess(result)
                            recommendDynamicOffset = result.data.offset
                            recommendDynamicPage++
                        } else {
                            val exception = IllegalDataReturnException(result.message, result.code)
                            exception.printStackTrace()
                            onFailed(exception)
                        }
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        ToastUtils.showText("处理数据时出现问题")
                        onFailed(e)
                    }
                }

            })
    }

    fun getSpaceDynamic(
        isRefresh: Boolean,
        mid: Long,
        onFailed: (Exception) -> Unit,
        onSuccess: (DynamicList) -> Unit
    ) {
        if (isRefresh) recommendDynamicPage = 1
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space?timezone_offset=-480&host_mid=$mid&type=all${if (recommendDynamicOffset.isEmpty()) "" else "&offset=$recommendDynamicOffset"}&page=$recommendDynamicPage".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body?.string()?.debugToast().log()
                    try {
                        val result = Gson().fromJson(responseStr, DynamicList::class.java)
                        if (result.code == 0) {
                            onSuccess(result)
                            spaceDynamicOffset = result.data.offset
                            spaceDynamicPage++
                        } else {
                            val exception = IllegalDataReturnException(result.message, result.code)
                            exception.printStackTrace()
                            onFailed(exception)
                        }
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        ToastUtils.showText("处理数据时出现问题")
                        onFailed(e)
                    }
                }

            })
    }

    fun getDynamicDetail(
        dyId: String,
        onFailed: (Exception) -> Unit,
        onSuccess: (DynamicDetail) -> Unit
    ) {
        val request = Request.Builder()
            .url("https://api.bilibili.com/x/polymer/web-dynamic/v1/detail?timezone_offset=-480&id=$dyId")
            .get()
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
            .header("origin", "https://t.bilibili.com/")
            .header("referer", "https://t.bilibili.com/$dyId")
            .build()
        NetworkUtils.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body?.string()?.debugToast().log()
                try {
                    val result = Gson().fromJson(responseStr, DynamicDetail::class.java)
                    if (result.code == 0) {
                        onSuccess(result)
                    } else {
                        val exception = IllegalDataReturnException(result.message, result.code)
                        exception.printStackTrace()
                        onFailed(exception)
                    }
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    ToastUtils.showText("处理数据时出现问题")
                    onFailed(e)
                }
            }

        })
    }

    fun getDynamicComments(
        isRefresh: Boolean,
        dyId: String,
        type: Int,
        onFailed: (Exception) -> Unit,
        onSuccess: (VideoComment) -> Unit
    ) {
        if (isRefresh) commentPage = 1
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v2/reply/main?type=$type&oid=$dyId&sort=1&next=$commentPage",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseStr = response.body?.string()?.debugToast().log()
                    try {
                        val result = Gson().fromJson(responseStr, VideoComment::class.java)
                        if (result.code == 0) {
                            onSuccess(result)
                            commentPage++
                        } else {
                            val exception = IllegalDataReturnException(result.message, result.code)
                            exception.printStackTrace()
                            onFailed(exception)
                        }
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        ToastUtils.showText("处理数据时出现问题")
                        onFailed(e)
                    }
                }

            })
    }
}