package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.wear.compose.material.ScalingLazyColumn
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.manager.isRound
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.viewmodel.RecommendViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


class RecommendVideoFragment : Fragment() {
    val viewModel by viewModels<RecommendViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(context!!)
    }

    //TODO : Memory Leak
    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ComposeView).setContent {
            val videoList by viewModel.videoList.observeAsState()
            val isRefreshing by viewModel.isRefreshing.observeAsState()
            val refreshState = rememberSwipeRefreshState(isRefreshing ?: false)
            SwipeRefresh(
                state = refreshState,
                onRefresh = {
                    viewModel.getRecommendVideos(true)
                },
                modifier = Modifier.padding(if (isRound()) 8.dp else 0.dp)
            ) {
                if (SettingsManager.hasScrollVfx()) {
                    ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
                        videoList?.forEach {
                            item {
                                VideoUis.VideoCard(
                                    videoName = it.title,
                                    views = it.cover_left_text_1 + "  " + (it.badge ?: ""),
                                    uploader = it.args.up_name ?: "",
                                    coverUrl = it.cover,
                                    hasViews = true,
                                    clickable = it.goto == "av" || it.goto == "bangumi",
                                    videoBvid = if (it.player_args?.aid != null) VideoUtils.av2bv("av${it.player_args.aid}") else "",
                                    context = context!!,
                                    isBangumi = it.goto == "bangumi",
                                    epid = it.param
                                )
                            }
                        }
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.getRecommendVideos(false)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxSize()
                    ) {
                        videoList?.forEach {
                            item {
                                VideoUis.VideoCard(
                                    videoName = it.title,
                                    views = it.cover_left_text_1 + (it.badge ?: ""),
                                    uploader = it.args.up_name ?: "",
                                    coverUrl = it.cover,
                                    hasViews = true,
                                    clickable = it.goto == "av" || it.goto == "bangumi",
                                    videoBvid = if (it.player_args?.aid != null) VideoUtils.av2bv("av${it.player_args.aid}") else "",
                                    context = context!!,
                                    isBangumi = it.goto == "bangumi",
                                    epid = it.param
                                )
                            }
                        }
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.getRecommendVideos(false)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.videoList.value.isNullOrEmpty()) {
            viewModel.getRecommendVideos(true)
        }
    }

    fun refresh() {
        viewModel.getRecommendVideos(true)
    }
}