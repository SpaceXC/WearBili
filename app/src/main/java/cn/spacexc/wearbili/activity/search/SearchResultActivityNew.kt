package cn.spacexc.wearbili.activity.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.VideoUis
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.viewmodel.SearchViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class SearchResultActivityNew : AppCompatActivity() {
    val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyword = getKeyword() ?: ""
        viewModel.searchVideo(keyword, true)
        setContent {
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "搜索结果",
                onBack = ::finish
            ) {
                val isRefreshing by viewModel.isRefreshing.observeAsState()
                val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing ?: false)
                //val searchData by viewModel.searchedVideosData.observeAsState()
                val searchedUser by viewModel.searchedUser
                val searchedMediaFt by viewModel.searchedMediaFt
                val searchedVideo by viewModel.searchedVideo
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
                                    avatar = "http:${it.upic}"
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

    @Composable
    fun UserCard(name: String, uid: Long, sign: String, avatar: String, pendant: String = "") {
        val localDensity = LocalDensity.current
        var pendentHeight by remember {
            mutableStateOf(0.dp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickVfx {
                    Intent(this, SpaceProfileActivity::class.java).apply {
                        putExtra("userMid", uid)
                        startActivity(this)
                    }
                }
                .border(
                    width = 0.1f.dp,
                    color = Color(112, 112, 112, 70),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(color = Color(36, 36, 36, 100)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(2f)
                    ) {
                        if (pendant.isEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(avatar)
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
                                    .data(avatar)
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
                                .data(pendant)
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
                        Text(
                            text = name,
                            fontFamily = puhuiFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                            //modifier = Modifier.scale(collapsingState.toolbarState.progress)
                        )
                        if (sign.isNotEmpty()) {
                            Text(
                                text = sign,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.8f)
                                    .animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 300
                                        )
                                    ),
                                maxLines = 2,
                                fontFamily = puhuiFamily,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}