package cn.spacexc.wearbili.utils

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Html
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.other.RequireNetworkActivity
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
        .cookieJar(object : CookieJar {
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

    fun getUrlWithLC(url: String, callback: Callback) {
        val currentTime = System.currentTimeMillis().toString()
        val request = Request.Builder()
            .url(url)
            .get()
            .header("X-LC-Id", "MAE7LopsPz1kgP3deSjzQ67g-gzGzoHsz")
            .header(
                "X-LC-Sign",
                "${EncryptUtils.md5("${currentTime}VuirpQYiGiekok2L03M9NX4o")},$currentTime"
            )
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

    fun postUrlWithoutBody(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .post(FormBody.Builder().build())
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun requireRetry(callback: () -> Unit) {
        val intent = Intent(Application.context, RequireNetworkActivity::class.java)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val callbackObj = RequireNetworkActivity.RetryCallback()
            callbackObj.callback = object : RequireNetworkActivity.RetryCallback.Retry {
                override fun onRetry() {
                    callback.invoke()
                }

            }
            putExtra("callback", callbackObj as Parcelable)
            Application.context?.startActivity(intent)
        }
    }

    /**
     * From CSDN https://blog.csdn.net/qq_16131393/article/details/51565278
     * MODIFIED BY XC-QAN
     */
    fun imageGetter(size: Int): Html.ImageGetter {
        return Html.ImageGetter { source ->
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



