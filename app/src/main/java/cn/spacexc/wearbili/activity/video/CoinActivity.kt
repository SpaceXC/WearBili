package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.BaseData
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val bvid = intent.getStringExtra("bvid")
            val aid = intent.getLongExtra("aid", 0L)
            val currentCoinCount = intent.getIntExtra("coinCount", 0)
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "投币",
                onBack = ::finish
            ) {
                var coinCount by remember {
                    mutableStateOf(if (currentCoinCount == 1) 1 else 0)
                }
                val alpha22 by animateFloatAsState(targetValue = if (coinCount == 1) 1f else .5f)
                val alpha33 by animateFloatAsState(targetValue = if (coinCount == 2) 1f else .5f)
                val buttonColor by animateColorAsState(
                    targetValue = if (coinCount != 0) Color(
                        254,
                        103,
                        154,
                        128
                    ) else Color(36, 36, 36, 128)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "给视频${bvid}投币",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontFamily = puhuiFamily,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.coin_22_x1),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .alpha(alpha22)
                                .clickVfx {
                                    if (currentCoinCount < 2) {
                                        coinCount = if (coinCount == 1)
                                            0
                                        else
                                            1
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )
                        Image(
                            painter = painterResource(id = R.drawable.coin_33_x2),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .alpha(alpha33)
                                .clickVfx {
                                    if (currentCoinCount == 0) {
                                        coinCount = if (coinCount == 2)
                                            0
                                        else
                                            2
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                    if (currentCoinCount == 2) {
                        Text(
                            text = "达到投币上限啦",
                            textAlign = TextAlign.Center,
                            color = BilibiliPink,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    AnimatedVisibility(
                        visible = coinCount != 0,
                        enter = slideInVertically {
                            it / 2
                        },
                        exit = slideOutVertically {
                            it / 2
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickVfx {
                                    coinVideo(coinCount, aid)
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = Color(30, 30, 30, 230))
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = CenterHorizontally
                            //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
                        ) {
                            Text(
                                text = "投${coinCount}个币",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    private fun coinVideo(count: Int, aid: Long) {
        UserManager.coinVideo(count, aid, object : NetworkUtils.ResultCallback<BaseData> {
            override fun onSuccess(result: BaseData, code: Int) {
                if (code == 0) {
                    MainScope().launch {
                        ToastUtils.showText("投币成功")
                        finish()
                    }
                }
            }

            override fun onFailed(e: Exception?) {
                if (e is IOException) {
                    MainScope().launch {
                        ToastUtils.showText("网络异常")
                    }
                }
            }

        })
    }
}