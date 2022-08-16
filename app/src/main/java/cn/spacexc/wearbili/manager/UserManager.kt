package cn.spacexc.wearbili.manager

import android.net.Uri
import cn.spacexc.wearbili.dataclass.user.AccessKeyGetter
import cn.spacexc.wearbili.utils.NetworkUtils
import com.google.gson.Gson
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
        val body: RequestBody = FormBody.Builder()
            .add("fid", mid.toString())
            .add("act", "1")
            .add("re_src", followSource.toString())
            .add("csrf", CookiesManager.getCookieByName("bili_jct")!!)
            .build()
        NetworkUtils.postUrl("https://api.bilibili.com/x/relation/modify", body, callback)
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
        if (!UserManager.isLoggedIn()) return false
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

    fun getStarFolderList(callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/v3/fav/folder/created/list-all?up_mid=${getUid()}&jsonp=jsonp",
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

    fun getFollowedUserByGroup(groupId: Int, page: Int, callback: Callback) {
        NetworkUtils.getUrl(
            "https://api.bilibili.com/x/relation/tag?tagid=$groupId&pn=$page",
            callback
        )
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

    //@Throws(IOException::class)
    /*fun saveAccessKey(cookie: String?): String? {
        try {
            var url = "https://passport.bilibili.com/login/app/third?api=http://link.acg.tv/forum.php&appkey=27eb53fc9058f8c3&sign=67ec798004373253d60114caaad89a8c"

            NetworkUtils.getUrl(url, object : Callback{
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                }

            })

            url =
                JSONObject(response.body!!.string()).getJSONObject("data").getString("confirm_uri")
            val client: OkHttpClient = Builder().connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false).build()
            var requestBuilder: Builder = Builder().url(url)
            var i = 0
            while (i < headers2.size) {
                requestBuilder = requestBuilder.addHeader(headers2[i], headers2[i + 1])
                i += 2
            }
            val request: Request = requestBuilder.build()
            response = client.newCall(request).execute()
            val url_location = response.header("location")
            return url_location!!.substring(
                url_location.indexOf("access_key=") + 11,
                url_location.indexOf("&", url_location.indexOf("access_key="))
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }*/
}