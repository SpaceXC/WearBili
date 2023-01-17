package cn.spacexc.wearbili.activity.search

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.UserCard
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.viewmodel.SearchViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class SearchResultActivityNew : AppCompatActivity() {
    val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyword = getKeyword().log() ?: ""
        viewModel.searchVideo(keyword, true)
        setContent {
            val searchedUser by viewModel.searchedUser.observeAsState()
            val searchedMediaFt by viewModel.searchedMediaFt.observeAsState()
            val searchedBangumi by viewModel.searchedMediaBangumi.observeAsState()
            val searchedVideo by viewModel.searchedVideo.observeAsState()
            val isError by viewModel.isError.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "搜索结果",
                onBack = ::finish,
                isLoading = searchedUser == null && searchedMediaFt == null && searchedVideo == null && searchedBangumi == null,
                isError = isError == true,
                errorRetry = {
                    viewModel.isError.value = false
                    viewModel.searchVideo(keyword, true)
                }
            ) {
                Crossfade(targetState = searchedUser?.isEmpty() == true && searchedMediaFt?.isEmpty() == true && searchedVideo?.isEmpty() == true) {
                    if (it) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.empty),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "今天真是寂寞如雪啊",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        val isRefreshing by viewModel.isRefreshing.observeAsState()
                        val refreshState =
                            rememberSwipeRefreshState(isRefreshing = isRefreshing ?: false)
                        //val searchData by viewModel.searchedVideosData.observeAsState()

                        SwipeRefresh(
                            state = refreshState,
                            refreshTriggerDistance = 40.dp,
                            onRefresh = { viewModel.searchVideo(keyword, true) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                searchedUser?.forEach {
                                    item {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        UserCard(
                                            name = it.uname,
                                            uid = it.mid,
                                            sign = it.usign,
                                            avatar = "http:${it.upic}",
                                            context = this@SearchResultActivityNew,
                                            officialType = it.officialVerify.type,
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                                searchedBangumi?.forEach {
                                    item {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        VideoUis.BangumiCard(
                                            bangumiName = it.title.replace(
                                                "<em class=\"keyword\">",
                                                ""
                                            )
                                                .replace("</em>", ""),
                                            cover = it.cover,
                                            areaInfo = "${it.areas}, ${it.indexShow}",
                                            description = it.desc,
                                            context = this@SearchResultActivityNew,
                                            id = it.seasonid.toString(),
                                            idType = ID_TYPE_SSID
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                                searchedMediaFt?.forEach {
                                    item {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        VideoUis.BangumiCard(
                                            bangumiName = it.title.replace(
                                                "<em class=\"keyword\">",
                                                ""
                                            )
                                                .replace("</em>", ""),
                                            cover = it.cover,
                                            areaInfo = "${it.areas}, ${it.indexShow}",
                                            description = it.desc,
                                            context = this@SearchResultActivityNew,
                                            id = it.seasonid.toString(),
                                            idType = ID_TYPE_SSID
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                                searchedVideo?.forEach {
                                    item {
                                        VideoUis.VideoCard(
                                            videoName = it.title.replace(
                                                "<em class=\"keyword\">",
                                                ""
                                            )
                                                .replace("</em>", ""),
                                            uploader = it.author,
                                            views = it.play.toShortChinese(),
                                            coverUrl = "https:${it.pic}",
                                            //badge = if (it.is_union_video == 1) "合作" else if (it.is_live_playback == 1) "直播回放" else if (it.is_pay == 1) "付费" else "",
                                            videoBvid = it.bvid,
                                            context = this@SearchResultActivityNew,
                                            clickable = true
                                        )
                                    }
                                }
                                item {
                                    LaunchedEffect(key1 = Unit, block = {
                                        viewModel.searchVideo(keyword)
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getKeyword(): String? {
        return if (intent.getStringExtra("keyword").isNullOrEmpty()) {
            intent.data?.getQueryParameter("keyword")
        } else {
            intent.getStringExtra("keyword")
        }
    }
}