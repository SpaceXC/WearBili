package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.viewmodel.HotViewModel

/*
 * Created by XC on 2022/10/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class VideoRankingActivity : AppCompatActivity() {
    val viewModel by viewModels<HotViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getRankingList()
        setContent {
            val rankingList by viewModel.rankingList.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "全站排行榜",
                onBack = ::finish,
                isLoading = rankingList == null
            ) {
                if (SettingsManager.hasScrollVfx()) {
                    ScalingLazyColumn(
                        contentPadding = PaddingValues(
                            top = 2.dp,
                            bottom = 8.dp,
                            start = 14.dp,
                            end = 14.dp
                        )
                    ) {
                        item {
                            Text(
                                text = rankingList?.data?.note ?: "",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                modifier = Modifier
                                    .alpha(0.8f)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        rankingList?.data?.list?.forEach {
                            item {
                                VideoUis.VideoCard(
                                    videoName = it.title,
                                    uploader = it.owner.name,
                                    views = it.stat.view.toShortChinese(),
                                    coverUrl = it.pic,
                                    videoBvid = it.bvid,
                                    clickable = true,
                                    context = this@VideoRankingActivity
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 2.dp,
                            bottom = 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                    ) {
                        item {
                            Text(
                                text = rankingList?.data?.note ?: "",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                modifier = Modifier
                                    .alpha(0.8f)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        rankingList?.data?.list?.forEach {
                            item {
                                VideoUis.VideoCard(
                                    videoName = it.title,
                                    uploader = it.owner.name,
                                    views = it.stat.view.toShortChinese(),
                                    coverUrl = it.pic,
                                    videoBvid = it.bvid,
                                    clickable = true,
                                    context = this@VideoRankingActivity
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}