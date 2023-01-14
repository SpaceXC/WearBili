package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.dynamic.Dynamic
import cn.spacexc.wearbili.dataclass.dynamic.dynamicepisode.EpisodeCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * ```
 * Created by XC-Qan on 2022/7/15.
 *
 * I'm very cute so please be nice to my code!
 *
 * 给！爷！写！注！释！
 *
 * 给！爷！写！注！释！
 *
 * 给！爷！写！注！释！
 *
 *
 * -------有感而发：--------
 *
 * 哇是谁发明了动态这种登c啊
 *
 * 十几种类型解析个*啊
 *
 * 啊B还是不用整那么丰富啊喂
 *
 * 现在data class才生成了4组
 *
 *
 * 猴年马月才能做完啊
 *
 *   ————XC 2022.07.15
 *
 * ------猴年马月更新-----
 *
 * 嘶。。。貌似也没那么麻烦
 *
 * Kotlin宝贝我爱你QwQ～～～
 *
 * kotlin是真的方便耶
 *
 * --------20221122-----------
 *
 * 怎么说
 *
 * compose yyds属于是了
 *
 *   ————XC 2022.11.22
 *
 * api来源（省的我找不到了）：https://github.com/SocialSisterYi/bilibili-API-collect/issues/109
 *
 * 大爱易姐！
 *
 * 以及其他开源仓库的贡献者们！
 * ``
 */

@Deprecated(
    message = "垃圾api设计，什么鬼玩意，谁用谁傻逼，json里面套json，哪个天才想的，被开了吧已经",
    replaceWith = ReplaceWith("DynamicManagerNew", "cn.spacexc.wearbili.manager.DynamicManagerNew"),
    level = DeprecationLevel.WARNING
)
class DynamicManager {
    var lastDynamicId: Long = 0

    var lastSpaceDynamicId: Long = 0

    /**
     * 1 - 转发
     *
     * 2 - 图文
     *
     * 4 - 文字
     *
     * 8 - 投稿
     *
     * 4098 - 番剧/影视
     */
    val type = "268435455,1,2,4,8,4098"    //前面那个268435455我也不知道为什么要加，反正不加就报错，github上看到的

    fun getRecommendDynamics(callback: DynamicResponseCallback) {
        if (!UserManager.isLoggedIn()) return
        val url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?" +
                "uid=${UserManager.getUid()}" +
                "&type=$type"
        url.debugToast("动态请求")
        NetworkUtils.getUrl(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(call, e)
                    e.debugToast("网络错误")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val str = response.body?.string().log()
                        MainScope().launch {
                            str?.debugToast("动态返回数据")
                            var code = 0
                            val tempList = dynamicListProcessor(str) {
                                code = it
                            }
                            tempList.debugToast("动态列表")
                            //Log.d(TAG, "onResponse: $tempList")
                            if (tempList.isNotEmpty()) {
                                lastDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                                lastDynamicId.debugToast("")
                                callback.onSuccess(tempList, code)
                            } else {
                                callback.onSuccess(emptyList(), code)
                            }
                        }

                    } catch (e: Exception) {
                        //TODO Ignored
                        e.printStackTrace()
                        MainScope().launch {
                            e.debugToast("数据处理错误")
                        }
                        callback.onFailed(call, e)
                    }

                }

            })

    }

    fun getMoreDynamic(callback: DynamicResponseCallback) {
        NetworkUtils.getUrl("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_history?&offset_dynamic_id=$lastDynamicId&type=$type",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        var code = 0
                        val tempList = dynamicListProcessor(response.body?.string()) {
                            code = it
                        }
                        //Log.d(TAG, "onResponse: $tempList")
                        lastDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                        callback.onSuccess(tempList, code)
                    } catch (e: Exception) {
                        Log.e(TAG, "onResponse: ${e.cause}", e)
                        callback.onFailed(call, e)
                        e.printStackTrace()
                    }

                }

            }
        )
    }

    fun getSpaceDynamics(mid: Long, callback: DynamicResponseCallback) {
        if (!UserManager.isLoggedIn()) return
        val url =
            "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?host_uid=$mid&type=$type".log(
                "space dynamic url"
            )
        url.debugToast("动态请求")
        NetworkUtils.getUrl(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(call, e)
                    e.debugToast("网络错误")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val str = response.body?.string().log()
                        MainScope().launch {
                            str?.debugToast("动态返回数据")
                            var code = 0
                            val tempList = dynamicListProcessor(str) {
                                code = it
                            }
                            tempList.debugToast("动态列表")
                            //Log.d(TAG, "onResponse: $tempList")
                            if (tempList.isNotEmpty()) {
                                lastSpaceDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                                lastSpaceDynamicId.debugToast("last dynamic id")
                                callback.onSuccess(tempList, code)
                            } else {
                                callback.onSuccess(emptyList(), code)
                            }
                        }

                    } catch (e: Exception) {
                        //TODO Ignored
                        e.printStackTrace()
                        MainScope().launch {
                            e.debugToast("数据处理错误")
                        }
                        callback.onFailed(call, e)
                    }

                }

            })

    }

    fun getMoreSpaceDynamic(mid: Long, callback: DynamicResponseCallback) {
        NetworkUtils.getUrl(
            "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?host_uid=$mid&type=$type&offset_dynamic_id=${
                lastSpaceDynamicId.log(
                    "call: last dynamic id"
                )
            }".log("more space dynamic url"),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        var code = 0
                        val tempList = dynamicListProcessor(response.body?.string()) {
                            code = it
                        }
                        //Log.d(TAG, "onResponse: $tempList")
                        if (tempList.isNotEmpty()) {
                            lastSpaceDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                        }
                        callback.onSuccess(tempList, code)
                    } catch (e: Exception) {
                        Log.e(TAG, "onResponse: ${e.cause}", e)
                        e.printStackTrace()
                        callback.onFailed(call, e)
                    }

                }
            }
        )
    }

    fun getDynamicDetails(dynamicId: String, callback: Callback) {
        NetworkUtils.getUrl(
            "http://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/get_dynamic_detail?dynamic_id=$dynamicId",
            callback
        )
    }

    fun dynamicListProcessor(response: String?, callback: (Int) -> Unit): List<Card> {
        val result = Gson().fromJson(response, Dynamic::class.java)
        callback.invoke(result.code)
        if (result.code != 0) {
            return emptyList()
        }
        if (result.data.has_more == 0) {
            return emptyList()
        }
        val tempList = mutableListOf<Card>()
        if (result.data.cards.isNullOrEmpty()) {
            return emptyList()
        }
        for (dynamic in result.data.cards) {
            when (dynamic.desc.type) {
                1 -> {
                    val card =
                        Gson().fromJson(dynamic.card, ForwardShareCard::class.java)
                    when (dynamic.desc.orig_type) {
                        1 -> {
                            val orig = Gson().fromJson(
                                card.origin,
                                ForwardShareCard::class.java
                            )
                            card.originObj = orig
                        }
                        2 -> {
                            val orig =
                                Gson().fromJson(card.origin, ImageCard::class.java)
                            card.originObj = orig
                        }
                        4 -> {
                            val orig =
                                Gson().fromJson(card.origin, TextCard::class.java)
                            card.originObj = orig
                        }
                        8 -> {
                            val orig =
                                Gson().fromJson(card.origin, VideoCard::class.java)
                            card.originObj = orig
                        }
                        4098 -> {
                            val orig =
                                Gson().fromJson(card.origin, EpisodeCard::class.java)
                            card.originObj = orig
                        }
                    }
                    dynamic.cardObj = card
                    tempList.add(dynamic)
                }
                2 -> {
                    val card = Gson().fromJson(dynamic.card, ImageCard::class.java)
                    dynamic.cardObj = card
                    tempList.add(dynamic)
                }
                4 -> {
                    val card = Gson().fromJson(dynamic.card, TextCard::class.java)
                    dynamic.cardObj = card
                    tempList.add(dynamic)
                }
                8 -> {
                    val card = Gson().fromJson(dynamic.card, VideoCard::class.java)
                    dynamic.cardObj = card
                    tempList.add(dynamic)
                }
                4098 -> {
                    val card = Gson().fromJson(dynamic.card, EpisodeCard::class.java)
                    dynamic.cardObj = card
                    tempList.add(dynamic)
                }
            }
        }
        return tempList
    }

    fun dynamicProcessor(dynamic: Card): Card? {
        when (dynamic.desc.type) {
            1 -> {
                val card =
                    Gson().fromJson(dynamic.card, ForwardShareCard::class.java)
                when (dynamic.desc.orig_type) {
                    1 -> {
                        val orig = Gson().fromJson(
                            card.origin,
                            ForwardShareCard::class.java
                        )
                        card.originObj = orig
                    }
                    2 -> {
                        val orig =
                            Gson().fromJson(card.origin, ImageCard::class.java)
                        card.originObj = orig
                    }
                    4 -> {
                        val orig =
                            Gson().fromJson(card.origin, TextCard::class.java)
                        card.originObj = orig
                    }
                    8 -> {
                        val orig =
                            Gson().fromJson(card.origin, VideoCard::class.java)
                        card.originObj = orig
                    }
                }
                dynamic.cardObj = card
                return dynamic
            }
            2 -> {
                val card = Gson().fromJson(dynamic.card, ImageCard::class.java)
                dynamic.cardObj = card
                return dynamic
            }
            4 -> {
                val card = Gson().fromJson(dynamic.card, TextCard::class.java)
                dynamic.cardObj = card
                return dynamic
            }
            8 -> {
                val card = Gson().fromJson(dynamic.card, VideoCard::class.java)
                dynamic.cardObj = card
                return dynamic
            }
        }
        return null
    }

    fun getCommentsByLikes(type: Int, dynamicId: Long, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v2/reply/main?type=$type&oid=$dynamicId&sort=1&next=$page",
            callback
        )
    }

    interface DynamicResponseCallback {
        fun onFailed(call: Call, e: Exception)
        fun onSuccess(dynamicCards: List<Card>, code: Int)
    }
}