package cn.spacexc.wearbili.manager

import android.net.Uri
import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.dataclass.BaseData
import cn.spacexc.wearbili.dataclass.HashSalt
import cn.spacexc.wearbili.dataclass.history.History
import cn.spacexc.wearbili.dataclass.star.result.FavoriteResult
import cn.spacexc.wearbili.dataclass.user.AccessKeyGetter
import cn.spacexc.wearbili.dataclass.user.spacevideo.UserSpaceVideo
import cn.spacexc.wearbili.utils.EncryptUtils
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object UserManager {
    fun getUserCookie(): String? = CookiesManager.getCookieByName("SESSDATA")

    fun isLoggedIn(): Boolean =
        !CookiesManager.getCookieByName("SESSDATA").isNullOrEmpty() &&
                !CookiesManager.getCookieByName("bili_jct").isNullOrEmpty() &&
                !CookiesManager.getCookieByName("DedeUserID").isNullOrEmpty()

    fun getUid(): String? = CookiesManager.getCookieByName("DedeUserID")

    fun getCurrentUser(callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/space/myinfo", callback)
    }

    fun getUserById(mid: Long, callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/space/acc/info?mid=$mid", callback)
    }

    fun getUserFans(mid: Long, callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/web-interface/card?mid=$mid", callback)
    }

    fun subscribeUser(mid: Long, followSource: Int, callback: Callback) {
        if (UserManager.isLoggedIn()) {
            val body: RequestBody = FormBody.Builder()
                .add("fid", mid.toString())
                .add("act", "1")
                .add("re_src", followSource.toString())
                .add("csrf", CookiesManager.getCookieByName("bili_jct")!!)
                .build()
            NetworkUtils.postUrl("https://api.bilibili.com/x/relation/modify", body, callback)
        } else {
            ToastUtils.showText("你还没有登录哦")
        }
    }

    fun deSubscribeUser(mid: Long, followSource: Int, callback: Callback) {
        val body: RequestBody = FormBody.Builder()
            .add("fid", mid.toString())
            .add("act", "2")
            .add("re_src", followSource.toString())
            .add("csrf", CookiesManager.getCookieByName("bili_jct")!!)
            .build()
        NetworkUtils.postUrl("https://api.bilibili.com/x/relation/modify", body, callback)
    }

    fun getWatchLater(callback: Callback) {
        NetworkUtils.getUrl("http://api.bilibili.com/x/v2/history/toview", callback)
    }

    fun deleteVideoFromWatchLater(aid: Long, callback: Callback): Boolean {
        if (!isLoggedIn()) return false
        val body: RequestBody = FormBody.Builder()
            .add("aid", aid.toString())
            .add("csrf", CookiesManager.getCsrfToken()!!)
            .build()
        NetworkUtils.postUrl("http://api.bilibili.com/x/v2/history/toview/del", body, callback)
        return true
    }

    fun getStarFolderData(id: Long, callback: Callback) {
        NetworkUtils.getUrl("http://api.bilibili.com/x/v3/fav/folder/info?media_id=$id", callback)
    }

    fun getStarFolderList(callback: Callback, aid: Long = 0L) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v3/fav/folder/created/list-all?up_mid=${getUid()}&jsonp=jsonp${if (aid != 0L) "&type=2&rid=$aid" else ""}",
            callback
        )
    }

    fun getStarFolderItemList(folderId: Long, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v3/fav/resource/list?media_id=$folderId&pn=$page&ps=20&order=mtime&type=2&platform=web",
            callback
        )
    }

    fun getFollowGroups(callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/relation/tags", callback)
    }

    fun getFollowedUserByGroup(groupId: Long, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/relation/tag?tagid=$groupId&pn=$page",
            callback
        )
    }

    fun logout(callback: NetworkUtils.ResultCallback<Boolean>) {
        val body = FormBody.Builder()
            .add("biliCSRF", CookiesManager.getCsrfToken()!!)
            .build()
        NetworkUtils.postUrl("http://passport.bilibili.com/login/exit/v2", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), BaseData::class.java)
                callback.onSuccess(result.code == 0)
            }

        })
    }

    fun getAccessKey(callback: NetworkUtils.ResultCallback<String>) {
        NetworkUtils.getUrl(
            "https://passport.bilibili.com/login/app/third?appkey=27eb53fc9058f8c3&api=http://link.acg.tv/forum.php&sign=67ec798004373253d60114caaad89a8c",
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result =
                        Gson().fromJson(response.body?.string(), AccessKeyGetter::class.java)
                    NetworkUtils.getUrlWithoutRedirect(result.data.confirm_uri, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            callback.onFailed(e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isRedirect) {
                                callback.onSuccess(
                                    Uri.parse(response.headers["location"])
                                        .getQueryParameter("access_key")!!
                                )
                            }

                            //callback.onSuccess(response.request.url.queryParameter("access_key")!!)
                        }

                    })
                }

            })
    }

    @Deprecated("Api url invalid")
    fun loginWithPassword(username: String, password: String) {
        NetworkUtils.getUrl("http://passport.bilibili.com/login?act=getkey", object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), HashSalt::class.java)
                val pwd = EncryptUtils.rsaEncrypt("${result.hash}$password", result.key)
                NetworkUtils.getUrl(
                    "https://account.bilibili.com/api/login/v2?userid=$username&pwd=${pwd}",
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.d(TAG, "onResponse: ${response.body?.string()}")
                        }

                    })
            }

        })
    }

    fun getAccessKey(): String = SharedPreferencesUtils.getString("accessKey", "")

    fun getUserSpaceDetail(mid: Long) {
        val baseUrl = "http://app.bilibili.com/x/v2/space"
        val params =
            "access_key=${getAccessKey()}&appkey=${ConfigurationManager.configurations["appKey"]}&build=${ConfigurationManager.configurations["build"]}&mobi_app=${ConfigurationManager.configurations["mobi_app"]}&platform=${ConfigurationManager.configurations["platform"]}&ps=20&ts=" + (System.currentTimeMillis() / 1000).toInt() + "&vmid=" + mid
        val sign: String = EncryptUtils.getAppSign(EncryptUtils.AppSignType.TYPE_COMMON, params)
        val url = "$baseUrl?$params&sign=$sign"
        url.log()
    }

    fun getHistory(viewAtTimeStamp: Long, callback: NetworkUtils.ResultCallback<History>) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/web-interface/history/cursor?max=0&view_at=$viewAtTimeStamp&business=".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), History::class.java)
                    callback.onSuccess(result)
                }

            })
    }

    fun getUserSpaceVideo(
        mid: Long,
        page: Int,
        keyword: String = "",
        callback: NetworkUtils.ResultCallback<UserSpaceVideo>
    ) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/space/arc/search?mid=$mid&pn=$page&ps=20&order=pubdate&order_avoided=true&jsonp=jsonp${if (keyword.isNotEmpty()) "&keyword=$keyword" else ""}".log(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val result =
                            Gson().fromJson(response.body?.string(), UserSpaceVideo::class.java)
                        callback.onSuccess(result, result.code)
                    } catch (e: Exception) {
                        callback.onFailed(e)
                    }
                }
            }
        )
    }

    fun addOrRemoveVideoToFavorite(
        aid: Long,
        idsToAdd: List<Long>,
        idsToDelete: List<Long>,
        callback: NetworkUtils.ResultCallback<FavoriteResult>
    ) {
        val bodyBuilder = FormBody.Builder().add("rid", aid.toString()).add("type", "2")
            .add("csrf", CookiesManager.getCsrfToken() ?: "")
        if (idsToAdd.isNotEmpty()) {
            bodyBuilder.add("add_media_ids", idsToAdd.joinToString(separator = ","))
        }
        if (idsToDelete.isNotEmpty()) {
            bodyBuilder.add("del_media_ids", idsToDelete.joinToString(separator = ",").log())
        }
        val body = bodyBuilder.build()
        NetworkUtils.postUrl(
            "http://api.bilibili.com/x/v3/fav/resource/deal",
            body,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailed(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val str = response.body?.string()
                    try {
                        val result = Gson().fromJson(str, FavoriteResult::class.java)
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

    fun coinVideo(count: Int, aid: Long, callback: NetworkUtils.ResultCallback<BaseData>) {
        val body = FormBody.Builder()
            .add("access_key", getAccessKey())
            .add("aid", aid.toString())
            .add("multiply", count.toString())
            .build()
        NetworkUtils.postUrl("http://app.bilibili.com/x/v2/view/coin/add", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body?.string()
                try {
                    val result = Gson().fromJson(str, BaseData::class.java)
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