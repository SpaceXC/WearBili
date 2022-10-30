package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.dataclass.hot.rankinglist.RankingList
import cn.spacexc.wearbili.utils.NetworkUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

object HotManager {
    fun getRankingList(callback: NetworkUtils.ResultCallback<RankingList>) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/web-interface/ranking/v2?rid=0&type=all",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), RankingList::class.java)
                    callback.onSuccess(result)
                }

            })
    }
}
