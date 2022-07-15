package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.dynamic.Dynamic
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.Desc
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.DynamicTypeForwardShare
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
 */

object DynamicManager {

    fun getRecommendDynamics(){
        NetworkUtils.getUrl("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_new?" +
                "uid=480816699" +
                "&type=268435455,1,2,4,8",
            object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                //TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body?.string()
                val result = Gson().fromJson(str, Dynamic::class.java)
                val tempList = mutableListOf<Any?>()
                for (dynamic in result.data.cards){
                    when(dynamic.desc.type){
                        1 -> {
                            val card = Gson().fromJson(dynamic.card, ForwardShareCard::class.java)
                            when(dynamic.desc.orig_type){
                                1 -> {
                                    val orig = Gson().fromJson(card.origin, ForwardShareCard::class.java)
                                    card.originObj = orig
                                }
                                2 -> {
                                    val orig = Gson().fromJson(card.origin, ImageCard::class.java)
                                    card.originObj = orig
                                }
                                4 -> {
                                    val orig = Gson().fromJson(card.origin, TextCard::class.java)
                                    card.originObj = orig
                                }
                                8 -> {
                                    val orig = Gson().fromJson(card.origin, VideoCard::class.java)
                                    card.originObj = orig
                                }
                            }
                            tempList.add(card)
                        }
                        2 -> {
                            val card = Gson().fromJson(dynamic.card, ImageCard::class.java)
                            tempList.add(card)
                        }
                        4 -> {
                            val card = Gson().fromJson(dynamic.card, TextCard::class.java)
                            tempList.add(card)
                        }
                        8 -> {
                            val card = Gson().fromJson(dynamic.card, VideoCard::class.java)
                            tempList.add(card)
                        }
                    }
                }
                Log.d(TAG, "onResponse: $tempList")
            }

        })
        /**
         * 1 - 转发
         * 2 - 图文
         * 4 - 文字
         * 8 - 投稿
         */
    }
}