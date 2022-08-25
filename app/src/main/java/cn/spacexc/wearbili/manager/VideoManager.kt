package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.utils.LogUtils.logWithGeneric
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToastWithGeneric
import cn.spacexc.wearbili.utils.VideoUtils
import okhttp3.*
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
        val url =
            if (UserManager.getAccessKey() != null) "http://app.bilibili.com/x/v2/feed/index?access_key=${UserManager.getAccessKey()}" else "http://app.bilibili.com/x/v2/feed/index"
        Log.d(TAG, "getRecommendVideo: $url")
        NetworkUtils.getUrl(
            url,
            callback
        )
        Log.d(Application.getTag(), "getRecommendVideo: " + CookiesManager.getCookies())
    }

    fun getVideoById(id: String?, callback: Callback) {
        if (id == null) {
            ToastUtils.makeText("视频不见了").show()
        } else {
            NetworkUtils.getUrl("https://api.bilibili.com/x/web-interface/view?bvid=$id", callback)
        }
    }

    fun getVideoUrl(bvid: String, cid: Long, callback: Callback) {
        //NetworkUtils.getUrl("http://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&fnval=1", callback)
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&platform=web&qn=80",
            callback
        )
    }

    fun getVideoMp4Url(bvid: String, cid: Long, callback: Callback) {
        //NetworkUtils.getUrl("http://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&fnval=1", callback)
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&fnval=1&platform=web",
            callback
        )
    }

    fun getDanmaku(cid: Long, callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/v1/dm/list.so?oid=$cid", callback)
    }

    fun searchVideo(keyword: String, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=$keyword&page=$page".debugToastWithGeneric()
                .logWithGeneric(),
            callback
        )
    }

    fun getCommentsByLikes(aid: Long, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v2/reply/main?type=1&oid=$aid&sort=1&next=$page",
            callback
        )
    }

    fun getOnlineCount(bvid: String, cid: Long, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/player/online/total?bvid=$bvid&cid=$cid",
            callback
        )
    }

    fun getVideoParts(bvid: String?, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/player/pagelist?bvid=$bvid",
            callback
        )
    }

    fun uploadVideoViewingProgress(bvid: String, cid: Long, progress: Int) {
        if (UserManager.getUserCookie() != null && CookiesManager.getCookieByName("bili_jct") != null) {
            val body: RequestBody = FormBody.Builder()
                .add("aid", VideoUtils.bv2av(bvid))
                .add("cid", cid.toString())
                .add("progress", progress.toString())
                .add("platform", "android")
                .add("csrf", CookiesManager.getCookieByName("bili_jct")!!)
                .build()
            NetworkUtils.postUrl(
                "https://api.bilibili.com/x/v2/history/report",
                body,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(Application.getTag(), "onFailure: 上报播放进度失败")

                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(Application.getTag(), "onResponse: 上报播放进度成功")
                        response.close()
                    }

                })
        }
    }

    fun likeVideo(bvid: String, isLike: Boolean, callback: Callback): Boolean {
        if (!UserManager.isLoggedIn()) return false
        val operation = when (isLike) {
            true -> 2       //取消赞
            false -> 1      //点赞
        }
        val body: RequestBody = FormBody.Builder()
            .add("bvid", bvid)
            .add("like", operation.toString())
            .add("csrf", CookiesManager.getCsrfToken()!!)
            .build()
        NetworkUtils.postUrl("http://api.bilibili.com/x/web-interface/archive/like", body, callback)
        return true
    }

    fun isLiked(bvid: String, callback: Callback) {
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/web-interface/archive/has/like?bvid=$bvid",
            callback
        )
    }

    fun addToView(bvid: String, callback: Callback): Boolean {
        if (!UserManager.isLoggedIn()) return false
        val body: RequestBody = FormBody.Builder()
            .add("bvid", bvid)
            .add("csrf", CookiesManager.getCsrfToken()!!)
            .build()
        NetworkUtils.postUrl("http://api.bilibili.com/x/v2/history/toview/add", body, callback)
        return true
    }
}