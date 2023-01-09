package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ExoPlayerUtils
import cn.spacexc.wearbili.viewmodel.VideoCacheViewModel
import com.google.gson.Gson

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class NewVideoCacheActivity : AppCompatActivity() {
    val viewModel by viewModels<VideoCacheViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bvid = intent.getStringExtra("bvid") ?: ""
        val videoTitle = intent.getStringExtra("title") ?: ""
        val coverUrl = intent.getStringExtra("coverUrl") ?: ""
        val subtitleUrl = intent.getStringExtra("subtitleUrl") ?: ""
        setContent {
            val data = Gson().fromJson(
                intent.getStringExtra("data"),
                cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages::class.java
            )
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "缓存视频",
                onBack = ::finish
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    data.pages.forEachIndexed { index, page ->
                        item {
                            Column(modifier = Modifier) {
                                Column(
                                    modifier = Modifier
                                        .clickVfx {
                                            ExoPlayerUtils
                                                .getInstance(this@NewVideoCacheActivity)
                                                .downloadVideo(
                                                    coverUrl,
                                                    videoTitle,
                                                    "P${index.plus(1)} ${page.part}",
                                                    bvid,
                                                    page.cid,
                                                    subtitleUrl,
                                                    ::finish
                                                )
                                        }
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 0.1f.dp, color = Color(
                                                112,
                                                112,
                                                112,
                                                204
                                            ), shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(color = Color(36, 36, 36, 199))
                                        .padding(vertical = 12.dp, horizontal = 16.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "P${index.plus(1)} ${page.part}",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontFamily = puhuiFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.alpha(0.76f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}