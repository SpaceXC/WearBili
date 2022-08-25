package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.dataclass.LeanCloudUserSearch
import cn.spacexc.wearbili.manager.CookiesManager
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.LCManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
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

        val intent =
            Intent(this@SplashScreenActivity, MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        /*startActivity(intent)
        overridePendingTransition(
            R.anim.activity_fade_in,
            R.anim.activity_fade_out
        )*/
        //finish()
        NetworkUtils.getUrl("https://bilibili.com", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry { }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    initApp()
                }
            }

        })
        initApp()

        /*NetworkUtils.requireRetry {
            initApp()
        }*/
    }

    fun initApp() {
        val canSkip = false
        if (canSkip) {
            val intent =
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_fade_out
            )
            finish()
        }
        Log.d(TAG, "onCreate: ${UserManager.isLoggedIn()}")
        if (UserManager.isLoggedIn()) {
            lifecycleScope.launch {
                Log.d(TAG, "onCreate: ${UserManager.getUid()!!}")
                LCManager.isUserActivated(UserManager.getUid()!!, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        MainScope().launch {
                            ToastUtils.makeText("网络异常").show()
                            /*val intent = Intent(
                                this@SplashScreenActivity,
                                SplashScreenActivity::class.java
                            )
                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.activity_fade_in,
                                R.anim.activity_fade_out
                            )
                            finish()*/
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
                                            /*val intent = Intent(
                                                this@SplashScreenActivity,
                                                SplashScreenActivity::class.java
                                            )
                                            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.activity_fade_in,
                                                R.anim.activity_fade_out
                                            )
                                            finish()*/
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
        }
    }
}