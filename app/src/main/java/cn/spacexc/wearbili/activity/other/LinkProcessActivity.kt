package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.VideoUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2023/1/15.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class LinkProcessActivity : AppCompatActivity() {
    val state = MutableLiveData(0)  //0: pending  1: success  2: not supported yet  3: error
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyUrl()
        setContent {
            val loadingState by state.observeAsState()
            CirclesBackground.RegularBackgroundWithNoTitle {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (loadingState) {
                        0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.loading_2233),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "玩命加载中",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        2 -> {
                            Column(
                                modifier = Modifier
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
                                    text = "不支持跳转, 点击获取页面二维码",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        3 -> {
                            Column(
                                modifier = Modifier
                                    .clickVfx {
                                        state.value = 0
                                        verifyUrl()
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
                                    text = "加载失败了, 点击重试",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun verifyUrl() {
        val uri = getUrl()
        if (VideoUtils.isBV(uri.path?.replace("/", "") ?: "")) {
            state.value = 1
            startActivity(Intent(this@LinkProcessActivity, VideoActivity::class.java).apply {
                putExtra("videoId", uri.path?.replace("/", ""))
            })
            finish()
        } else if (uri.host != "b23.tv") {
            state.value = 2
        } else {
            NetworkUtils.getUrlWithoutRedirect(uri.toString(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        state.value = 3
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val location = Uri.parse(response.headers["location"])
                    MainScope().launch {
                        if (VideoUtils.isBV(location.path?.log()?.replace("/video/", "") ?: "")) {
                            state.value = 1
                            startActivity(
                                Intent(
                                    this@LinkProcessActivity,
                                    VideoActivity::class.java
                                ).apply {
                                    putExtra(
                                        "videoId",
                                        location.path?.log()?.replace("/video/", "")
                                    )
                                })
                            finish()
                        } else {
                            state.value = 2
                        }
                    }
                }
            })
        }
    }

    fun getUrl(): Uri {
        return intent.data ?: Uri.parse(intent.getStringExtra("url"))
    }
}