package cn.spacexc.wearbili.manager

import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG
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
    fun getCookies(): List<Cookie> {
        val cookieString = SharedPreferencesUtils.getString("cookies", "")
        if (cookieString.isNullOrEmpty()) return emptyList()

        val tempCookies = Gson().fromJson(cookieString, Cookies::class.java)
        return tempCookies.cookies
    }

    fun saveCookies(cookies: List<Cookie>) {
        val cookiesTemp = getCookies().toMutableList().unionCookies(cookies).toMutableList()
        val cookiesObj = Cookies(cookiesTemp)
        val cookieString = Gson().toJson(cookiesObj)
        SharedPreferencesUtils.saveString("cookies", cookieString)
    }

    fun deleteAllCookies() {
        SharedPreferencesUtils.delete("cookies")
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

    fun MutableList<Cookie>.unionCookies(secondList: List<Cookie>): List<Cookie> {
        val temp: MutableList<Cookie> = mutableListOf()
        val cookiesToAdd: MutableList<Cookie> = secondList.toMutableList()
        temp.addAll(this)
        if (temp.isEmpty()) {
            temp.addAll(secondList)
            return temp
        }
        temp.mapIndexed { index, cookie ->
            for (cookieToAdd in secondList) {
                if (cookie.name == cookieToAdd.name && cookie.path == cookieToAdd.path) {
                    temp[index] = cookieToAdd
                    cookiesToAdd.remove(cookieToAdd)
                }
            }
        }
        Log.d(TAG, "unionCookiesSecond: $secondList")
        Log.d(TAG, "unionCookiesTemp: $temp")
        temp.addAll(cookiesToAdd)
        return temp
    }

    fun getCsrfToken(): String? {
        return getCookieByName("bili_jct")
    }

    /*private fun List<Cookie>.unionCookies(cookies: List<Cookie>): List<Cookie> {
        *//*val temp = this.toMutableList()
        for (cookie in this.indices){
            for(cookiesAdd in cookies){
                if(this[cookie].name == cookiesAdd.name && this[cookie].domain == cookiesAdd.domain){
                    temp[cookie] = cookiesAdd
                }
                else{
                    temp.add(cookiesAdd)
                }
            }
        }*//*
        return cookies + this
    }*/
}