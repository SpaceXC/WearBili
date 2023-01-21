package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.viewmodel.WatchLaterViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class WatchLaterActivityNew : AppCompatActivity() {
    val viewModel by viewModels<WatchLaterViewModel>()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getWatchLater(true)
        setContent {
            val isError by viewModel.isError
            val isRefreshing by viewModel.isRefreshing
            val videoList by viewModel.watchLaterList.observeAsState()
            val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "稍后再看",
                onBack = ::finish,
                isLoading = videoList == null,
                isError = isError,
                errorRetry = { viewModel.isError.value = false; viewModel.getWatchLater(true) }) {
                Crossfade(targetState = videoList.isNullOrEmpty()) {
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
                                    text = "这里空空如也唔",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        SwipeRefresh(
                            state = refreshState,
                            onRefresh = { viewModel.getWatchLater(true) },
                            refreshTriggerDistance = 40.dp
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                videoList?.forEach { video ->
                                    item(key = video.bvid) {
                                        Box(modifier = Modifier.animateItemPlacement()) {
                                            val state = rememberDismissState()
                                            if (state.isDismissed(DismissDirection.EndToStart)) {
                                                viewModel.deleteWatchLater(video.aid)
                                            }
                                            SwipeToDismiss(
                                                state = state,
                                                background = { },
                                                directions = setOf(DismissDirection.EndToStart)
                                            ) {
                                                VideoUis.VideoCard(
                                                    videoName = video.title,
                                                    uploader = video.owner.name,
                                                    views = "看到${video.progress.secondToTime()}",
                                                    coverUrl = video.pic,
                                                    modifier = Modifier.animateItemPlacement(),
                                                    videoBvid = video.bvid
                                                )
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
    }
}