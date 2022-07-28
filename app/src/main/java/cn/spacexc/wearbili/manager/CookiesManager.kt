package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.utils.Cookies
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import com.google.gson.Gson
import com.microsoft.appcenter.analytics.Analytics
import okhttp3.Cookie

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object CookiesManager {
    fun getCookies() : List<Cookie>{
        val cookieString = SharedPreferencesUtils.getString("cookies", "")
        if(cookieString.isNullOrEmpty()) return emptyList()

        val tempCookies = Gson().fromJson(cookieString, Cookies::class.java)
        return tempCookies.cookies
    }

    fun saveCookies(cookies : List<Cookie>) {
        val tempCookie = Cookies(cookies)
        val cookiesObj = Cookies(getCookies().union(tempCookie.cookies.toSet()).toList())
        val cookieString = Gson().toJson(cookiesObj)
        SharedPreferencesUtils.saveString("cookies", cookieString)
    }

    fun getCookieByName(name: String): String? {
        val cookieList: List<Cookie> = getCookies()
        for (item in cookieList) {
            if (item.domain == "bilibili.com" && item.name == name) {
                return item.value
            }
        }
        return null
    }

    fun uploadCookies() {
        val properties = HashMap<String, String>()
        properties["DeviceInfo"] = DeviceManager.getDeviceName()!!
        properties["UploadTime"] = System.currentTimeMillis().toString()
        properties["HasCookies"] = getCookies().isNotEmpty().toString()
        for (cookie in getCookies()) {
            properties[cookie.name] = cookie.value
        }
        Analytics.trackEvent("Device Cookies Upload ${System.currentTimeMillis()}", properties)
    }

    fun getCsrfToken(): String? {
        return getCookieByName("bili_jct")
    }
}