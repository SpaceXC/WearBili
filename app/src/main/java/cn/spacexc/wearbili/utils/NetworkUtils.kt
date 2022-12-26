package cn.spacexc.wearbili.utils

import android.content.Intent
import android.graphics.drawable.Drawable
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
const val RESULT_RETRY = 1
const val USER_AGENT =
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"

class Cookies(var cookies: MutableList<Cookie>)

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

    private val clientWithoutRedirect = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return CookiesManager.getCookies()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                CookiesManager.saveCookies(cookies)
            }

        })
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    fun getUrlWithoutRedirect(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
            .build()
        clientWithoutRedirect.newCall(request).enqueue(callback)
    }

    fun getUrl(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
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
            .header("User-Agent", USER_AGENT)
            .build()
        client.newCall(request).enqueue(callback)
    }


    fun getUrlWithoutCallback(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
            .build()
        return client.newCall(request).execute()
    }

    fun postUrl(url: String, body: RequestBody, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
            .build()
        client.newCall(request).enqueue(callback)

    }

    fun postUrlWithoutBody(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .post(FormBody.Builder().build())
            .header("User-Agent", USER_AGENT)
            .header("x-bili-aurora-zone", "sh001")
            .header("x-bili-aurora-eid", "UlMFQVcABlAH")
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun requireRetry(callback: () -> Unit) {
        Intent(Application.context, RequireNetworkActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Application.context?.startActivity(this)
        }
    }

    /**
     * From [CSDN](https://blog.csdn.net/qq_16131393/article/details/51565278)
     *
     * MODIFIED BY XC-QAN
     */
    fun imageGetter(size: Int): Html.ImageGetter {
        return Html.ImageGetter { source ->
            val `is`: InputStream?
            try {
                `is` = URL(source).content as InputStream
                val d = Drawable.createFromStream(`is`, "src")
                d?.setBounds(
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

    interface ResultCallback<T> {
        fun onSuccess(result: T, code: Int = 0)
        fun onFailed(e: Exception?)
    }
}



