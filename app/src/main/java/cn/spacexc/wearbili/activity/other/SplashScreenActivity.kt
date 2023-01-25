package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class SplashScreenActivity : AppCompatActivity() {
    val isError = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash_screen)
        initApp()
        var clickCount = 0
        setContent {
            val errorState by isError
            CirclesBackground.RegularBackgroundWithNoTitle {
                Crossfade(targetState = errorState) {
                    if (it) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .clickVfx {
                                        val intent = Intent(
                                            this@SplashScreenActivity,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)
                                        overridePendingTransition(
                                            R.anim.activity_fade_in,
                                            R.anim.activity_fade_out
                                        )
                                        finish()
                                    }
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.loading_2233_error),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "无法与哔哩星球建立通讯\n点击继续进入应用",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.start_page_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickVfx {
                                        clickCount++
                                        if (clickCount == 1) ToastUtils.showText("再次点击即可快速进入")
                                        else {
                                            val intent = Intent(
                                                this@SplashScreenActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.activity_fade_in,
                                                R.anim.activity_fade_out
                                            )
                                            finish()
                                        }
                                    },
                                contentScale = ContentScale.FillBounds
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    private fun initApp() {
        if (!SharedPreferencesUtils.getBoolean("hasSetUp", false)) {
            val intent =
                Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(
                R.anim.activity_fade_in,
                R.anim.activity_fade_out
            )
            finish()
        } else {
            NetworkUtils.getUrl("https://bilibili.com", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        isError.value = true
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.close()
                    MainScope().launch {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
                        finish()
                    }
                }

            })
        }
    }
}