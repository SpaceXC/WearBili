package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.dataclass.dynamic.new.list.DynamicList
import cn.spacexc.wearbili.exception.IllegalDataReturnException
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
 * Created by XC on 2023/1/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DynamicManagerNew {
    var recommendDynamicOffset = 0L
    var recommendDynamicPage = 1

    fun getRecommendDynamic(
        isRefreshing: Boolean,
        onFailed: (Exception) -> Unit,
        onSuccess: (DynamicList) -> Unit
    ) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/all?timezone_offset=-480&type=all${if (recommendDynamicOffset == 0L) "" else "&offset=$recommendDynamicOffset"}&page=$recommendDynamicPage",
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