package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.VideoComment
import cn.spacexc.wearbili.dataclass.comment.reply.CommentReply
import cn.spacexc.wearbili.dataclass.search.Search
import cn.spacexc.wearbili.dataclass.video.rcmd.web.WebRecommendVideo
import cn.spacexc.wearbili.dataclass.video.state.CoinState
import cn.spacexc.wearbili.dataclass.video.state.FavState
import cn.spacexc.wearbili.dataclass.video.state.LikeState
import cn.spacexc.wearbili.dataclass.video.state.result.LikeResult
import cn.spacexc.wearbili.dataclass.videoDetail.VideoDetailInfo
import cn.spacexc.wearbili.utils.EncryptUtils
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToastWithGeneric
import cn.spacexc.wearbili.utils.VideoUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder

/**
 * Created by XC-Qan on 2022/6/10.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object VideoManager {
    /**
     * 叔叔真的好爱玩这几个接口啊...
     * 改了好多遍了
     */
    fun getRecommendVideo(callback: Callback) {
        val url =
            if (UserManager.getAccessKey().log("accessKey")
                    .isNotEmpty()
            ) "http://app.bilibili.com/x/v2/feed/index?access_key=${UserManager.getAccessKey()}&actionKey=appkey&appkey=27eb53fc9058f8c3&build=70000100&c_locale=zh-Hans_CN&column=1&disable_rcmd=0&flush=0&fnval=976&fnver=0&force_host=0&fourk=1&guidance=1&https_url_req=0&login_event=2&pull=1&qn=32&recsys_mode=0&s_locale=zh-Hans_CH&screen_window_type=0"
            else "http://app.bilibili.com/x/v2/feed/index?column=1"
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
                .log(),
            callback
        )
    }

    fun getCommentsByLikes(aid: Long, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v2/reply/main?type=1&oid=$aid&sort=1&next=$page".log(),
            callback
        )
    }

    fun getCommentsByLikes(
        aid: Long,
        page: Int,
        onFailed: (Exception?) -> Unit,
        onSuccess: (VideoComment) -> Unit
    ) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v2/reply/main?type=1&oid=$aid&sort=1&next=$page".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string().log()
                    try {
                        val result = Gson().fromJson(str, VideoComment::class.java)
                        if (result.code == 0) {
                            onSuccess(result)
                        } else {
                            onFailed(null)
                            MainScope().launch {
                                ToastUtils.showText("${result.code}: ${result.message}")
                                ToastUtils.debugToast(str.log() ?: "")
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        MainScope().launch {
                            ToastUtils.showText("请求错误")
                            ToastUtils.debugToast(str.log() ?: "")
                            onFailed(e)
                        }
                    }
                }

            })
    }

    fun getCommentReplies(
        aid: Long,
        rootCommentRpid: Long,
        page: Int,
        onFailed: (Exception?) -> Unit,
        onSuccess: (CommentReply) -> Unit
    ) {
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/v2/reply/reply?type=1&oid=$aid&root=$rootCommentRpid&pn=$page".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string().log()
                    try {
                        val result = Gson().fromJson(str, CommentReply::class.java)
                        if (result.code == 0) {
                            onSuccess(result)
                        } else {
                            onFailed(null)
                            MainScope().launch {
                                ToastUtils.showText("${result.code}: ${result.message}")
                                ToastUtils.debugToast(str.log() ?: "")
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        MainScope().launch {
                            ToastUtils.showText("请求错误")
                            ToastUtils.debugToast(str.log() ?: "")
                            onFailed(e)
                        }
                    }
                }

            })
    }

    fun getOnlineCount(bvid: String, cid: Long, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/player/online/total?bvid=$bvid&cid=$cid",
            callback
        )
    }

    /**
     * 貌似暂时没啥用，不过一定要用，不然传递太多东西可能会导致性能问题（或许？
     */
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

    fun likeVideo(
        bvid: String,
        isLike: Boolean,
        callback: NetworkUtils.ResultCallback<LikeResult>
    ) {
        if (!UserManager.isLoggedIn()) {
            ToastUtils.showText("你还没有登录捏")
            return
        }
        val operation = when (isLike) {
            true -> 2       //取消赞
            false -> 1      //点赞
        }
        val body: RequestBody = FormBody.Builder()
            .add("bvid", bvid)
            .add("like", operation.toString())
            .add("csrf", CookiesManager.getCsrfToken()!!)
            .build()
        NetworkUtils.postUrl(
            "http://api.bilibili.com/x/web-interface/archive/like",
            body,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), LikeResult::class.java)
                    callback.onSuccess(result, result.code)
                }

            }
        )
    }

    fun isLiked(bvid: String, callback: NetworkUtils.ResultCallback<LikeState>) {
        if (!UserManager.isLoggedIn()) return
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/web-interface/archive/has/like?bvid=$bvid",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), LikeState::class.java)
                    callback.onSuccess(result)
                }

            })
    }

    fun isCoined(bvid: String, callback: NetworkUtils.ResultCallback<CoinState>) {
        if (!UserManager.isLoggedIn()) return
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/web-interface/archive/coins?bvid=$bvid",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), CoinState::class.java)
                    callback.onSuccess(result)
                }

            })
    }

    fun isFavorite(bvid: String, callback: NetworkUtils.ResultCallback<FavState>) {
        if (!UserManager.isLoggedIn()) return
        NetworkUtils.getUrl(
            "http://api.bilibili.com/x/v2/fav/video/favoured?aid=$bvid",
            object : Callback {     //这接口就这么设计的我也不知道为啥bv和av都是aid（
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), FavState::class.java)
                    callback.onSuccess(result)
                }

            }
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

    fun getVideoInfo(bvid: String, callback: NetworkUtils.ResultCallback<VideoDetailInfo>) {
        val baseUrl = "https://app.bilibili.com/x/v2/view"
        val params =
            "access_key=${UserManager.getAccessKey()}&appkey=${ConfigurationManager.configurations["appKey"]}&build=${ConfigurationManager.configurations["build"]}&bvid=$bvid&mobi_app=${ConfigurationManager.configurations["mobi_app"]}&plat=0&platform=${ConfigurationManager.configurations["platform"]}&ts=${(System.currentTimeMillis() / 1000).toInt()}"
        val sign: String = EncryptUtils.getAppSign(EncryptUtils.AppSignType.TYPE_COMMON, params)
        val url = "$baseUrl?$params&sign=$sign"
        url.log()
        NetworkUtils.getUrl(url.log(), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result =
                        Gson().fromJson(response.body?.string().log(), VideoDetailInfo::class.java)
                    callback.onSuccess(result, result.code)
                } catch (e: Exception) {
                    callback.onFailed(e)
                }
            }

        })
    }

    fun searchVideoAll(keyword: String, page: Int, callback: NetworkUtils.ResultCallback<Search>) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/web-interface/search/all/v2?page=$page&single_column=0&keyword=${
                URLEncoder.encode(
                    keyword,
                    "UTF-8"
                )
            }".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string()
                    try {
                        val result = Gson().fromJson(str, Search::class.java)
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
            }
        )
    }

    fun getWebRecommend(callback: NetworkUtils.ResultCallback<WebRecommendVideo>) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/web-interface/index/top/rcmd?fresh_type=10&version=1&ps=8&fresh_idx=&fresh_idx_1h=&homepage_ver=1".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string().log()
                    try {
                        val result = Gson().fromJson(str, WebRecommendVideo::class.java)
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
                            callback.onFailed(e)
                        }
                    }
                }
            }
        )
    }
}