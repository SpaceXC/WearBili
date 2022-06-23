package cn.spacexc.wearbili.manager

import android.widget.Toast
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.utils.NetworkUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/6/10.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object VideoManager {
    fun getRecommendVideo(callback: Callback) {
        if (CookiesManager.getCookies().isEmpty()) NetworkUtils.getUrl(
            "https://bilibili.com",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    NetworkUtils.getUrl(
                        "https://api.bilibili.com/x/web-interface/index/top/rcmd?fresh_type=3&version=1&ps=10&fresh_idx=1&fresh_idx_1h=1&homepage_ver=1",
                        callback
                    )
                }
            })
        else {
            NetworkUtils.getUrl(
                "https://api.bilibili.com/x/web-interface/index/top/rcmd?fresh_type=3&version=1&ps=10&fresh_idx=1&fresh_idx_1h=1&homepage_ver=1",
                callback
            )
        }
    }

    fun getVideoById(id: String?, callback: Callback) {
        if (id == null) {
            Toast.makeText(Application.getContext(), "视频不见了", Toast.LENGTH_SHORT).show()
        } else {
            NetworkUtils.getUrl("http://api.bilibili.com/x/web-interface/view?bvid=$id", callback)
        }
    }

    fun getVideoUrl(bvid : String, cid : Long, callback: Callback){
        NetworkUtils.getUrl("http://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&fnval=1", callback)
    }

    fun getDanmaku(cid : Long, callback: Callback){
        NetworkUtils.getUrlComp("http://api.bilibili.com/x/v1/dm/list.so?oid=$cid", callback)
    }

    fun searchVideo(keyword : String, page : Int, callback: Callback){
        NetworkUtils.getUrl("http://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=$keyword&page=$page", callback)
    }

    fun getCommentsByLikes(aid : Long, page : Int, callback: Callback){
        NetworkUtils.getUrl("http://api.bilibili.com/x/v2/reply/main?type=1&oid=$aid&sort=1&pn=$page", callback)
    }
}