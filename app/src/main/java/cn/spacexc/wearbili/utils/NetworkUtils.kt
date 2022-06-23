package cn.spacexc.wearbili.utils

import cn.spacexc.wearbili.manager.CookiesManager
import okhttp3.*


/**
 * Created by XC-Qan on 2022/6/6.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class Cookies(var cookies: List<Cookie>)

object NetworkUtils {
    private val client = OkHttpClient.Builder()
        .cookieJar(object : CookieJar{
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return CookiesManager.getCookies()
            }
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                CookiesManager.saveCookies(cookies)
            }

        })
        .build()

    fun getUrl(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(callback)
    }
    fun getUrlComp(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun postUrl(url : String, body: RequestBody,callback: Callback){
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

}

