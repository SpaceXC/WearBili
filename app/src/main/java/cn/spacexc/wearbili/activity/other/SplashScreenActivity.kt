package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        /*val downloadRequest: DownloadRequest = DownloadRequest.Builder("BV1MJ411Q7zC", Uri.parse("https://upos-sz-mirrorcos.bilivideo.com/upgcxcode/33/46/132454633/132454633-1-208.mp4?e=ig8euxZM2rNcNbhanWdVhwdlhzuHhwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1670509693&gen=playurlv2&os=cosbv&oi=1972173940&trid=03be433d5aa144d2bd910bb47d91cf7eu&mid=0&platform=pc&upsig=8b26eb614f784f506172bea4d8714dc5&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&agrr=0&bw=380872&logo=80000000")).build()
        downloadRequest.toMediaItem()
        DownloadService.sendAddDownload(
            Application.context!!,
            cn.spacexc.wearbili.service.DownloadService::class.java,
            downloadRequest,
            false
        )*/

        /*startActivity(Intent(this, MainActivity::class.java))
        finish()*/


        if (!SharedPreferencesUtils.getBoolean("hasSetUp", false)) {
            val intent =
                Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.putExtra("id", "42962")
            //intent.putExtra("idType", ID_TYPE_SSID)
            startActivity(intent)
            overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_fade_out
            )
            finish()
        } else {
            NetworkUtils.getUrl("https://bilibili.com", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    NetworkUtils.requireRetry { }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.close()
                    MainScope().launch {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
                        finish()
                    }
                }

            })
        }

        //initApp()

        /*NetworkUtils.requireRetry {
            initApp()
        }*/
    }

    @Deprecated(message = "以后没有登录验证辣！！！")
    fun initApp() {
        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        finish()

        return
        //呜呼呼以后没有账号验证咯！！！ohhhhhh


        /*val canSkip = false
        if (canSkip) {
            val intent =
                Intent(this@SplashScreenActivity, BangumiActivity::class.java)
            intent.putExtra("epid", "312080")
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_fade_out
            )
            finish()
            return
        }
        Log.d(TAG, "onCreate: ${UserManager.isLoggedIn()}")
        if (UserManager.isLoggedIn()) {
            lifecycleScope.launch {
//                Log.d(TAG, "onCreate: ${UserManager.getUid()!!}")
                LCManager.isUserActivated(UserManager.getUid()!!, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        MainScope().launch {
                            ToastUtils.makeText("网络异常").show()
                            *//*val intent = Intent(
                                this@SplashScreenActivity,
                                SplashScreenActivity::class.java
                            )
                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.activity_fade_in,
                                R.anim.activity_fade_out
                            )
                            finish()*//*
                            NetworkUtils.requireRetry {
                                initApp()
                            }

                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        kotlin.runCatching {
                            val responseString = response.body?.string()
                            Log.d(TAG, "responseString: $responseString")
                            val result = Gson().fromJson(
                                responseString,
                                LeanCloudUserSearch::class.java
                            )
                            Log.d(TAG, "responseObject: $result")
                            if (!result.results.isNullOrEmpty()) {
                                val intent =
                                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.activity_fade_in,
                                    R.anim.activity_fade_out
                                )
                                finish()
                            } else {
                                val url =
                                    "http://passport.bilibili.com/login/exit/v2?biliCSRF=${CookiesManager.getCsrfToken()}"
                                NetworkUtils.postUrlWithoutBody(url, object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        MainScope().launch {
                                            ToastUtils.makeText("网络异常").show()
                                            *//*val intent = Intent(
                                                this@SplashScreenActivity,
                                                SplashScreenActivity::class.java
                                            )
                                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.activity_fade_in,
                                                R.anim.activity_fade_out
                                            )
                                            finish()*//*
                                            NetworkUtils.requireRetry {
                                                initApp()
                                            }
                                        }
                                    }

                                    override fun onResponse(
                                        call: Call,
                                        response: Response
                                    ) {
                                        CookiesManager.deleteAllCookies()
                                        MainScope().launch {
                                            ToastUtils.makeText("您没有使用权限,请重新登录").show()
                                            val intent = Intent(
                                                this@SplashScreenActivity,
                                                LoginActivity::class.java
                                            )
                                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.activity_fade_in,
                                                R.anim.activity_fade_out
                                            )
                                            finish()
                                            //TODO 到引导激活页面

                                        }
                                    }
                                })
                            }
                        }
                    }
                })
            }
        } else {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
            finish()
        }*/
    }
}