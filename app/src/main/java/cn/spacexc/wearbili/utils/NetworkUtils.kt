package cn.spacexc.wearbili.utils

import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import cn.spacexc.wearbili.manager.CookiesManager
import okhttp3.*
import java.io.InputStream
import java.net.URL


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

    fun getUrlWithoutCallback(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return client.newCall(request).execute()
    }

    fun postUrl(url: String, body: RequestBody, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

    /**
     * From CSDN https://blog.csdn.net/qq_33593432/article/details/118638944
     */
    fun imageGetter(size: Int): ImageGetter {
        return ImageGetter { source ->
            val `is`: InputStream?
            try {
                `is` = URL(source).content as InputStream
                val d = Drawable.createFromStream(`is`, "src")
                d.setBounds(
                    0, 0, size,
                    size
                )
                `is`.close()
                return@ImageGetter d
            } catch (e: Exception) {
                return@ImageGetter null
            }
        }
    }

}
