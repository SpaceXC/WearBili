package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.dynamic.Dynamic
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.utils.NetworkUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/7/15.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 *
 * -------有感而发：--------｜
 * 哇是谁发明了动态这种登si啊 ｜
 * 十几种类型解析个*啊       ｜
 * 啊B还是不用整那么丰富啊喂  ｜
 * 现在data class才生成了4组｜
 * 猴年马月才能做完啊        ｜
 *   ————XC 2022.07.15    ｜
 * -----------------------
 *
 * 嘶。。。貌似也没那么麻烦
 * Kotlin宝贝我爱你QwQ～～～
 * kotlin是真的方便耶
 *
 * api来源（省的我找不到了）：https://github.com/SocialSisterYi/bilibili-API-collect/issues/109
 * 大爱易姐！
 */

object DynamicManager {
    var lastDynamicId: Long = 0

    /**
     * 1 - 转发
     * 2 - 图文
     * 4 - 文字
     * 8 - 投稿
     */
    const val type = "268435455,1,2,4,8"    //前面那个268435455我也不知道为什么要加，反正不加就报错

    fun getRecommendDynamics(callback: DynamicResponseCallback) {

        NetworkUtils.getUrl("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?" +
                "uid=${UserManager.getUid()}" +
                "&type=$type",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        /*val str = response.body?.string()
                        val result = Gson().fromJson(str, Dynamic::class.java)
                        val tempList = mutableListOf<Card>()
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
                            }
                        }*/
                        val tempList = processWithDynamic(response.body?.string())
                        //Log.d(TAG, "onResponse: $tempList")
                        lastDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                        callback.onSuccess(tempList)
                    } catch (e: Exception) {
                        //TODO Ignored
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
                        /*val str = response.body?.string()
                        val result = Gson().fromJson(str, Dynamic::class.java)
                        val tempList = mutableListOf<Card>()
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
                            }
                        }*/
                        val tempList = processWithDynamic(response.body?.string())
                        //Log.d(TAG, "onResponse: $tempList")
                        lastDynamicId = tempList[tempList.size - 1].desc.dynamic_id
                        callback.onSuccess(tempList)
                    } catch (e: Exception) {
                        Log.e(TAG, "onResponse: ${e.cause}", e)
                    }

                }

            })
    }

    fun processWithDynamic(response: String?): List<Card> {
        val result = Gson().fromJson(response, Dynamic::class.java)
        val tempList = mutableListOf<Card>()
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
            }
        }
        return tempList
    }

    interface DynamicResponseCallback {
        fun onFailed(call: Call, e: IOException)
        fun onSuccess(dynamicCards: List<Card>)
    }
}