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
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
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
        return ComposeView(requireContext())
    }

    //TODO : Memory Leak
    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ComposeView).setContent {
            val appVideoList by viewModel.appVideoList.observeAsState()
            val webVideoList by viewModel.webVideoList.observeAsState()
            val isRefreshing by viewModel.isRefreshing.observeAsState()
            val refreshState = rememberSwipeRefreshState(isRefreshing ?: false)
            SwipeRefresh(
                state = refreshState,
                onRefresh = {
                    when (SettingsManager.getRecommendSource()) {
                        "app" -> viewModel.getAppRecommendVideos(true)
                        "web" -> viewModel.getWebRecommendVideos(true)
                    }

                },
                modifier = Modifier.padding(if (isRound()) 8.dp else 0.dp)
            ) {
                if (SettingsManager.hasScrollVfx()) {
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = viewModel.scalingLazyListState
                    ) {
                        when (SettingsManager.getRecommendSource()) {
                            "app" -> {
                                appVideoList?.forEach {
                                    if (it.goto == "av"/* || it.goto == "bangumi"*/) {
                                        item {
                                            VideoUis.VideoCard(
                                                videoName = it.title,
                                                views = it.cover_left_text_2 ?: "",
                                                uploader = it.args.up_name ?: "",
                                                coverUrl = it.cover ?: "",
                                                hasViews = true,
                                                clickable = true, //it.goto == "av" || it.goto == "bangumi",
                                                videoBvid = it.bvid,
                                                context = requireContext(),
                                                isBangumi = false, //it.goto == "bangumi",
                                                epid = it.param,
                                                badge = it.badge ?: ""
                                            )
                                        }
                                    }
                                }
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.getAppRecommendVideos(false)
                                    }
                                }
                            }
                            "web" -> {
                                webVideoList?.forEach {
                                    if (it.goto == "av") {
                                        item {
                                            VideoUis.VideoCard(
                                                videoName = it.title,
                                                uploader = it.owner?.name ?: "",
                                                views = it.stat?.view?.toShortChinese() ?: "",
                                                coverUrl = it.pic,
                                                hasViews = true,
                                                clickable = true,
                                                videoBvid = it.bvid,
                                                context = requireContext(),
                                            )
                                        }
                                    }
                                }
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.getWebRecommendVideos(false)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxSize(), state = viewModel.lazyListState
                    ) {
                        when (SettingsManager.getRecommendSource()) {
                            "app" -> {
                                appVideoList?.forEach {
                                    if (it.goto == "av") {
                                        item {
                                            VideoUis.VideoCard(
                                                videoName = it.title,
                                                views = it.cover_left_text_2 ?: "",
                                                uploader = it.args.up_name ?: "",
                                                coverUrl = it.cover ?: "",
                                                hasViews = true,
                                                clickable = true,   //it.goto == "av" || it.goto == "bangumi",
                                                videoBvid = it.bvid,
                                                context = requireContext(),
                                                isBangumi = false,  //it.goto == "bangumi",
                                                epid = it.param,
                                                badge = it.cover_badge ?: "",

                                                )
                                        }
                                    }
                                }
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.getAppRecommendVideos(false)
                                    }
                                }
                            }
                            "web" -> {
                                webVideoList?.forEach {
                                    if (it.goto == "av") {
                                        item {
                                            VideoUis.VideoCard(
                                                videoName = it.title,
                                                uploader = it.owner?.name ?: "",
                                                views = it.stat?.view?.toShortChinese() ?: "",
                                                coverUrl = it.pic,
                                                hasViews = true,
                                                clickable = true,
                                                videoBvid = it.bvid,
                                                context = requireContext(),
                                            )
                                        }
                                    }
                                }
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.getWebRecommendVideos(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (SettingsManager.getRecommendSource()) {
            "app" -> {
                if (viewModel.appVideoList.value.isNullOrEmpty()) {
                    viewModel.getAppRecommendVideos(true)
                }
            }
            "web" -> {
                if (viewModel.webVideoList.value.isNullOrEmpty()) {
                    viewModel.getWebRecommendVideos(true)
                }
            }
        }

    }

    fun refresh() {
        when (SettingsManager.getRecommendSource()) {
            "app" -> viewModel.getAppRecommendVideos(true)
            "web" -> viewModel.getWebRecommendVideos(true)
        }
    }
}