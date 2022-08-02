package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.LeanCloudUserSearch
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.LCManager
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
        if (UserManager.isLoggedIn()) {
            lifecycleScope.launch {
                UserManager.getUid()?.let {
                    LCManager.isUserActivated(it, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            MainScope().launch {
                                ToastUtils.makeText("网络异常").show()
                                val intent = Intent(
                                    this@SplashScreenActivity,
                                    SplashScreenActivity::class.java
                                )
                                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.activity_fade_in,
                                    R.anim.activity_fade_out
                                )
                                finish()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val result = Gson().fromJson(
                                response.body?.string(),
                                LeanCloudUserSearch::class.java
                            )
                            if (result.results.isNotEmpty()) {
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
                        }

                    })
                }
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