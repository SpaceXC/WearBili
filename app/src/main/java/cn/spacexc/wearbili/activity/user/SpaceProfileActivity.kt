package cn.spacexc.wearbili.activity.user

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.ui.*
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.utils.parseColor
import cn.spacexc.wearbili.viewmodel.UserSpaceViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/11/25.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SpaceProfileActivity : AppCompatActivity() {
    val viewModel: UserSpaceViewModel by viewModels()

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userMid = intent.getLongExtra("userMid", 0)
        viewModel.getUser(userMid)
        viewModel.getVideos(userMid, true)
        viewModel.getDynamic(userMid)
        setContent {
            val localDensity = LocalDensity.current
            val user by viewModel.user.observeAsState()
            var pendentHeight by remember {
                mutableStateOf(0.dp)
            }
            val collapsingState = rememberCollapsingToolbarScaffoldState()
            val pagerState = rememberPagerState()
            val userVideos by viewModel.videos.observeAsState()
            val dynamicList by viewModel.dynamicCardList.observeAsState()
            val scope = rememberCoroutineScope()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "个人空间", onBack = { finish() }
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)) {
                    CollapsingToolbarScaffold(
                        state = collapsingState,
                        modifier = Modifier.fillMaxSize(),
                        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
                        toolbarModifier = Modifier.verticalScroll(rememberScrollState()),
                        toolbar = {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.dp)
                            )   //不！要！删！掉！这个是用来做伸缩topbar的！！很重要！
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .parallax(0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 0.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.weight(2f)
                                    ) {
                                        if (user?.data?.pendant?.image_enhance.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user?.data?.face)
                                                    .placeholder(R.drawable.akari).crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(6.dp)
                                                    .clip(CircleShape)
                                                    .aspectRatio(1f)

                                            )
                                        } else {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user?.data?.face)
                                                    .crossfade(true).placeholder(R.drawable.akari)
                                                    .build(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    //.fillMaxWidth()
                                                    .size(
                                                        pendentHeight.times(0.6f),
                                                        pendentHeight.times(0.6f)
                                                    )
                                                    .clip(CircleShape)
                                                    .aspectRatio(1f)
                                            )
                                            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                                .data(user?.data?.pendant?.image_enhance)
                                                .crossfade(true)
                                                .build(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .onGloballyPositioned {
                                                        pendentHeight = with(localDensity) {
                                                            it.size.height.toDp()
                                                        }
                                                    }
                                                    .aspectRatio(1f)
                                            )

                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(4f)
                                            .fillMaxWidth()
                                    ) {
                                        var maxLines by remember {
                                            mutableStateOf(1)
                                        }
                                        Text(
                                            text = user?.data?.name ?: "加载中",
                                            fontFamily = puhuiFamily,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = parseColor(user?.data?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" })
                                            //modifier = Modifier.scale(collapsingState.toolbarState.progress)
                                        )
                                        if (!user?.data?.sign.isNullOrEmpty()) {
                                            Text(
                                                text = user?.data?.sign ?: "",
                                                color = Color.White,
                                                modifier = Modifier
                                                    .alpha(0.8f)
                                                    .animateContentSize(
                                                        animationSpec = tween(
                                                            durationMillis = 300
                                                        )
                                                    )
                                                    .clickable {
                                                        maxLines =
                                                            if (maxLines == 1) Int.MAX_VALUE else 1
                                                    },
                                                maxLines = maxLines,
                                                fontFamily = puhuiFamily,
                                                fontSize = 12.sp,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(360.dp)
                                                )
                                                .background(BilibiliPink)
                                                .padding(
                                                    start = 10.dp,
                                                    end = 13.dp,
                                                    top = 3.dp,
                                                    bottom = 5.dp
                                                )

                                        ) {
                                            var buttonTextHeight by remember {
                                                mutableStateOf(0.dp)
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(buttonTextHeight)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "关注",
                                                color = Color.White,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.onGloballyPositioned {
                                                    buttonTextHeight = with(localDensity) {
                                                        it.size.height.toDp()
                                                    }
                                                }, fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 0.1.dp,
                                    color = Color(112, 112, 112, 70),
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    )
                                )
                                .background(Color(36, 36, 36, 100))
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(
                                        rememberScrollState()
                                    ), contentPadding = PaddingValues(
                                    start = 6.dp, end = 6.dp, top = 8.dp, bottom = 6.dp
                                )
                            ) {
                                item {
                                    TabItem(text = "投稿", isSelected = pagerState.currentPage == 0) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    }
                                }
                                item {
                                    TabItem(text = "动态", isSelected = pagerState.currentPage == 1) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    }
                                }

                            }
                            HorizontalPager(count = 2, state = pagerState) { page ->
                                when (page) {
                                    0 -> {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentPadding = PaddingValues(
                                                horizontal = 6.dp
                                            )
                                        ) {
                                            userVideos?.forEach {
                                                item {
                                                    VideoUis.VideoCard(
                                                        videoName = it.title,
                                                        uploader = it.author,
                                                        views = it.play.toShortChinese(),
                                                        coverUrl = it.pic,
                                                        badge = if (it.is_union_video == 1) "合作" else if (it.is_live_playback == 1) "直播回放" else if (it.is_pay == 1) "付费" else "",
                                                        videoBvid = it.bvid,
                                                        context = this@SpaceProfileActivity,
                                                        clickable = true
                                                    )
                                                }
                                            }
                                            item {
                                                LaunchedEffect(key1 = Unit, block = {
                                                    viewModel.getVideos(userMid, false)
                                                })
                                            }
                                        }
                                    }
                                    1 -> {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            //contentPadding = PaddingValues(vertical = 4.dp)
                                        ) {
                                            dynamicList?.forEach { card ->
                                                item {
                                                    DynamicCard(
                                                        posterAvatar = card.desc.user_profile.info.face,
                                                        posterName = card.desc.user_profile.info.uname,
                                                        posterNameColor = if (!card.desc.user_profile.vip.nickname_color.isNullOrEmpty()) Color(
                                                            android.graphics.Color.parseColor(
                                                                card.desc.user_profile.vip.nickname_color
                                                            )
                                                        ) else Color.White,
                                                        postTime = (card.desc.timestamp * 1000).toDateStr(
                                                            "MM-dd HH:mm"
                                                        ),
                                                        card = card,
                                                        context = this@SpaceProfileActivity
                                                    )
                                                }
                                            }
                                            item {
                                                LaunchedEffect(key1 = Unit) {
                                                    viewModel.getMoreDynamic(userMid)
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

    @Composable
    fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
        val color by animateColorAsState(
            targetValue = if (isSelected) BilibiliPink else Color.Transparent,
            animationSpec = tween(durationMillis = 300)
        )
        val borderWidth by animateDpAsState(
            targetValue = if (isSelected) 0.dp else 2.dp,
            animationSpec = tween(durationMillis = 300)
        )
        Row {
            Text(
                text = text,
                color = Color.White,
                fontFamily = puhuiFamily,
                modifier = Modifier
                    .border(
                        width = borderWidth, color = Color(
                            255,
                            255,
                            255,
                            61
                        ), shape = RoundedCornerShape(360.dp)
                    )
                    .clip(RoundedCornerShape(360.dp))
                    .background(color)
                    .padding(start = 12.dp, end = 13.dp, top = 3.dp, bottom = 4.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { onClick() }),
                fontWeight = FontWeight.Medium, fontSize = 14.sp

            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}