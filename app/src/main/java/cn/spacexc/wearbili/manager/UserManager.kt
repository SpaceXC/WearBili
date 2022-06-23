package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.utils.NetworkUtils
import okhttp3.Callback

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object UserManager {
    fun getUserCookie() : String? = CookiesManager.getCookieByName("SESSDATA")

    fun getCurrentUser(callback: Callback){
        NetworkUtils.getUrl("https://api.bilibili.com/x/space/myinfo", callback)
    }
}