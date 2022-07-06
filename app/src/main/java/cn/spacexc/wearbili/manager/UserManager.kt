package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.utils.NetworkUtils
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.RequestBody

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object UserManager {
    fun getUserCookie(): String? = CookiesManager.getCookieByName("SESSDATA")

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
}