package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.viewmodel.RecommendViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


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
            val isError by viewModel.isError.observeAsState()
            val refreshState = rememberSwipeRefreshState(isRefreshing ?: false)
            val localDensity = LocalDensity.current
            var containerHeight by remember {
                mutableStateOf(0.dp)
            }
            var currentExpandItem by remember {
                mutableStateOf(-1)
            }
            val scope = rememberCoroutineScope()
            var isAnimating = false
            Crossfade(targetState = !(appVideoList.isNullOrEmpty() && webVideoList.isNullOrEmpty())) { loading ->
                if (loading) {
                    SwipeRefresh(
                        state = refreshState,
                        onRefresh = {
                            when (SettingsManager.getRecommendSource()) {
                                "app" -> viewModel.getAppRecommendVideos(true)
                                "web" -> viewModel.getWebRecommendVideos(true)
                            }
                        }, modifier = Modifier.onGloballyPositioned {
                            containerHeight =
                                with(localDensity) { it.size.height.toDp().times(0.9f) }
                        }
                    ) {
                        if (SettingsManager.hasScrollVfx()) {
                            ScalingLazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = viewModel.scalingLazyListState
                            ) {
                                when (SettingsManager.getRecommendSource()) {
                                    "app" -> {
                                        appVideoList?.forEachIndexed { index, it ->
                                            if (it.goto == "av"/* || it.goto == "bangumi"*/) {
                                                item(key = it.param) {
                                                    VideoUis.VideoCard(
                                                        videoName = it.title,
                                                        views = it.cover_left_text_2 ?: "",
                                                        uploader = it.args.up_name ?: "",
                                                        coverUrl = it.cover ?: "",
                                                        hasViews = true,
                                                        clickable = true, //it.goto == "av" || it.goto == "bangumi",
                                                        videoBvid = it.bvid
                                                            ?: VideoUtils.av2bv("av${it.param}"),
                                                        context = requireContext(),
                                                        isBangumi = false, //it.goto == "bangumi",
                                                        epid = it.param,
                                                        badge = it.badge ?: "",
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
                                                item(key = it.bvid) {
                                                    VideoUis.VideoCard(
                                                        videoName = it.title,
                                                        uploader = it.owner?.name ?: "",
                                                        views = it.stat?.view?.toShortChinese()
                                                            ?: "",
                                                        coverUrl = it.pic,
                                                        hasViews = true,
                                                        clickable = true,
                                                        videoBvid = it.bvid,
                                                        context = requireContext()
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
                            if (viewModel.lazyListState.isScrollInProgress && !isAnimating) {
                                currentExpandItem = -1
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                                    .fillMaxSize(), state = viewModel.lazyListState
                            ) {
                                when (SettingsManager.getRecommendSource()) {
                                    "app" -> {
                                        appVideoList?.forEachIndexed { index, it ->
                                            if (it.goto == "av") {
                                                item(key = it.param) {
                                                    VideoUis.VideoCard(
                                                        videoName = it.title,
                                                        views = it.cover_left_text_2 ?: "",
                                                        uploader = it.args.up_name ?: "",
                                                        coverUrl = it.cover ?: "",
                                                        hasViews = true,
                                                        clickable = true,   //it.goto == "av" || it.goto == "bangumi",
                                                        videoBvid = it.bvid
                                                            ?: VideoUtils.av2bv("av${it.param}"),
                                                        context = requireContext(),
                                                        isBangumi = false,  //it.goto == "bangumi",
                                                        epid = it.param,
                                                        badge = it.cover_badge ?: "",
                                                        expandHeight = containerHeight,
                                                        isExpand = currentExpandItem == index,
                                                        onExpandBack = { currentExpandItem = -1 },
                                                        onLongClickExpand = {
                                                            scope.launch {
                                                                isAnimating = true
                                                                viewModel.lazyListState.animateScrollToItem(
                                                                    index
                                                                )
                                                                isAnimating = false
                                                            }
                                                            currentExpandItem = index
                                                        }
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
                                        webVideoList?.forEachIndexed { index, it ->
                                            if (it.goto == "av") {
                                                item(key = it.bvid) {
                                                    VideoUis.VideoCard(
                                                        videoName = it.title,
                                                        uploader = it.owner?.name ?: "",
                                                        views = it.stat?.view?.toShortChinese()
                                                            ?: "",
                                                        coverUrl = it.pic,
                                                        hasViews = true,
                                                        clickable = true,
                                                        videoBvid = it.bvid,
                                                        context = requireContext(),
                                                        expandHeight = containerHeight,
                                                        isExpand = currentExpandItem == index,
                                                        onExpandBack = { currentExpandItem = -1 },
                                                        onLongClickExpand = {
                                                            scope.launch {
                                                                isAnimating = true
                                                                viewModel.lazyListState.animateScrollToItem(
                                                                    index
                                                                )
                                                                isAnimating = false
                                                            }
                                                            currentExpandItem = index
                                                        }
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
                } else {
                    Crossfade(targetState = isError) { error ->
                        if (error == true) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Center)
                                        .clickVfx {
                                            viewModel.isError.value = false
                                            refresh()
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = cn.spacexc.wearbili.R.drawable.loading_2233_error),
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "加载失败, 点击重试",
                                        color = Color.White,
                                        fontFamily = puhuiFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = cn.spacexc.wearbili.R.drawable.loading_2233),
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "玩命加载中",
                                        color = Color.White,
                                        fontFamily = puhuiFamily,
                                        fontWeight = FontWeight.Medium
                                    )
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