package cn.spacexc.wearbili.activity.search

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.UserCard
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.viewmodel.SearchViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class SearchResultActivityNew : AppCompatActivity() {
    val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyword = getKeyword() ?: ""
        viewModel.searchVideo(keyword, true)
        setContent {
            val searchedUser by viewModel.searchedUser
            val searchedMediaFt by viewModel.searchedMediaFt
            val searchedVideo by viewModel.searchedVideo
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "搜索结果",
                onBack = ::finish,
                isLoading = searchedUser.isEmpty() && searchedMediaFt.isEmpty() && searchedVideo.isEmpty()
            ) {
                val isRefreshing by viewModel.isRefreshing.observeAsState()
                val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing ?: false)
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
                        searchedUser.forEach {
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
                        searchedMediaFt.forEach {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                VideoUis.BangumiCard(
                                    bangumiName = it.title.replace("<em class=\"keyword\">", "")
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
                        searchedVideo.forEach {
                            item {
                                VideoUis.VideoCard(
                                    videoName = it.title.replace("<em class=\"keyword\">", "")
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

    private fun getKeyword(): String? {
        return if (intent.getStringExtra("keyword").isNullOrEmpty()) {
            intent.data?.getQueryParameter("keyword")
        } else {
            intent.getStringExtra("keyword")
        }
    }
}