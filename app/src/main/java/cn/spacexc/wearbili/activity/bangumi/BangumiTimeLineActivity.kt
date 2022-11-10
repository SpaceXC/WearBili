package cn.spacexc.wearbili.activity.bangumi

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.viewmodel.BangumiViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

/*
 * Created by XC on 2022/10/29.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class BangumiTimeLineActivity : AppCompatActivity() {
    private val chineseNums = mapOf(
        0 to "日",
        1 to "一",
        2 to "二",
        3 to "三",
        4 to "四",
        5 to "五",
        6 to "六",
        7 to "日"
    )
    private val viewModel by viewModels<BangumiViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getTimeLine()
        setContent {
            val lazyState = rememberLazyListState()
            val timeline by viewModel.timeLine.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(title = "新番时间表", onBack = {
                finish()
            }) {
                if (SettingsManager.hasScrollVfx()) {
                    ScalingLazyColumn(
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = 8.dp,
                            start = 14.dp,
                            end = 14.dp
                        )
                    ) {
                        timeline?.result?.forEach {
                            item {
                                LaunchedEffect(key1 = Unit, block = {
                                    lazyState.animateScrollToItem(
                                        timeline?.result?.get(0)?.episodes?.size ?: 0
                                    )
                                })
                                Text(
                                    text = "${it.date} 星期${chineseNums[it.day_of_week]}",
                                    fontFamily = puhuiFamily,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .alpha(0.8f)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            it.episodes.forEach { episode ->
                                item {
                                    Column(modifier = Modifier.clickVfx {
                                        Intent(
                                            this@BangumiTimeLineActivity,
                                            BangumiActivity::class.java
                                        ).apply {
                                            putExtra("id", episode.season_id.toString())
                                            putExtra("idType", ID_TYPE_SSID)
                                            startActivity(this)
                                        }
                                    }) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(episode.cover)
                                                    .crossfade(true)
                                                    .build(),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    //.fillMaxHeight()
                                                    .fillMaxSize()
                                                    .aspectRatio(
                                                        0.75f,
                                                        matchHeightConstraintsFirst = true
                                                    )
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .align(Alignment.CenterVertically)
                                                    .animateContentSize(
                                                        animationSpec = tween(
                                                            durationMillis = 200
                                                        )
                                                    ),
                                                contentDescription = null
                                            )   //番剧封面
                                            Column(
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .fillMaxWidth()
                                                    .padding(start = 8.dp)
                                            ) {
                                                Text(
                                                    text = episode.title,
                                                    color = Color.White,
                                                    fontFamily = puhuiFamily,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = episode.pub_time,
                                                    fontFamily = puhuiFamily,
                                                    color = Color.Gray, fontSize = 12.sp
                                                )
                                                Text(
                                                    text = if (episode.pub_ts * 1000 >= System.currentTimeMillis()) "即将更新${episode.pub_index}" else "更新至${episode.pub_index}",
                                                    fontFamily = puhuiFamily,
                                                    color = Color.Gray, fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }   //番剧信息
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(8.dp)) {
                        timeline?.result?.forEach {
                            item {
                                LaunchedEffect(key1 = Unit, block = {
                                    lazyState.animateScrollToItem(
                                        timeline?.result?.get(0)?.episodes?.size ?: 0
                                    )
                                })
                                Text(
                                    text = "${it.date} 星期${chineseNums[it.day_of_week]}",
                                    fontFamily = puhuiFamily,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.alpha(0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            it.episodes.forEach { episode ->
                                item {
                                    Column(modifier = Modifier.clickVfx {
                                        Intent(
                                            this@BangumiTimeLineActivity,
                                            BangumiActivity::class.java
                                        ).apply {
                                            putExtra("id", episode.season_id.toString())
                                            putExtra("idType", ID_TYPE_SSID)
                                            startActivity(this)
                                        }
                                    }) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(episode.cover)
                                                    .crossfade(true)
                                                    .build(),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    //.fillMaxHeight()
                                                    .fillMaxSize()
                                                    .aspectRatio(
                                                        0.75f,
                                                        matchHeightConstraintsFirst = true
                                                    )
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .align(Alignment.CenterVertically)
                                                    .animateContentSize(
                                                        animationSpec = tween(
                                                            durationMillis = 200
                                                        )
                                                    ),
                                                contentDescription = null
                                            )   //番剧封面
                                            Column(
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .fillMaxWidth()
                                                    .padding(start = 8.dp)
                                            ) {
                                                Text(
                                                    text = episode.title,
                                                    color = Color.White,
                                                    fontFamily = puhuiFamily,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = episode.pub_time,
                                                    fontFamily = puhuiFamily,
                                                    color = Color.Gray, fontSize = 12.sp
                                                )
                                                Text(
                                                    text = if (episode.pub_ts * 1000 >= System.currentTimeMillis()) "即将更新${episode.pub_index}" else "更新至${episode.pub_index}",
                                                    fontFamily = puhuiFamily,
                                                    color = Color.Gray, fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }   //番剧信息
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}