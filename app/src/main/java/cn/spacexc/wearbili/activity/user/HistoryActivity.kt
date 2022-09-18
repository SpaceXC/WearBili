package cn.spacexc.wearbili.activity.user

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.ui.CirclesBackground.RegularBackgroundWithTitleAndBackArrow
import cn.spacexc.wearbili.ui.VideoUis.VideoCard
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.viewmodel.HistoryViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class HistoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<HistoryViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val historyList by viewModel.historyList.observeAsState()
            val isRefreshing by viewModel.isRefreshing.observeAsState()
            val refreshState = rememberSwipeRefreshState(isRefreshing ?: false)
            RegularBackgroundWithTitleAndBackArrow(onBack = { finish() }, title = "历史记录") {
                SwipeRefresh(
                    state = refreshState,
                    onRefresh = {
                        viewModel.getHistory(true)
                    }
                ){
                    if(SettingsManager.hasScrollVfx()){
                        ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
                            historyList?.forEach {
                                item {
                                    VideoCard(
                                        videoName = it.title,
                                        views = if(it.progress != -1) "看到 ${it.progress.secondToTime()}" else "已看完",
                                        uploader = it.author_name,
                                        coverUrl = if(it.cover.isNullOrEmpty()) it.covers[0] else it.cover,
                                        hasViews = it.history.business == "archive",
                                        clickable = it.history.business == "archive",
                                        videoBvid = it.history.bvid ?: "",
                                        context = this@HistoryActivity
                                    )
                                }
                            }
                            item {
                                LaunchedEffect(Unit) {
                                    viewModel.getHistory()
                                }
                            }
                        }
                    }
                    else{
                        LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxSize()) {
                            historyList?.forEach {
                                item {
                                    VideoCard(
                                        videoName = it.title,
                                        views = if(it.progress != -1) "看到 ${it.progress.secondToTime()}" else "已看完",
                                        uploader = it.author_name,
                                        coverUrl = if(it.cover.isNullOrEmpty()) it.covers[0] else it.cover,
                                        hasViews = it.history.business == "archive",
                                        clickable = it.history.business == "archive",
                                        videoBvid = it.history.bvid ?: "",
                                        context = this@HistoryActivity
                                    )
                                }
                            }
                            item {
                                LaunchedEffect(Unit) {
                                    viewModel.getHistory()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}