package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.image.PhotoViewActivity
import cn.spacexc.wearbili.activity.settings.ChooseSettingsActivity
import cn.spacexc.wearbili.activity.video.PlayOnPhoneActivity
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.RoundButtonDataNew
import cn.spacexc.wearbili.dataclass.bangumi.BangumiDetail
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.manager.isRound
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.RoundButton
import cn.spacexc.wearbili.ui.googleSansFamily
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ExoPlayerUtils
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.parseColor
import coil.compose.AsyncImage
import coil.request.ImageRequest

class BangumiInfoFragment : Fragment() {
    lateinit var activity: BangumiActivity
    /*private val btnListUpperRow = MutableLiveData(
        mutableListOf(
            RoundButtonData(R.drawable.ic_baseline_play_circle_outline_24, "播放", "播放"),
            RoundButtonData(R.drawable.ic_outline_thumb_up_24, "点赞", "点赞"),
            RoundButtonData(R.drawable.ic_outline_monetization_on_24, "投币", "投币"),
            RoundButtonData(R.drawable.ic_round_star_border_24, "收藏", "收藏"),
            RoundButtonData(R.drawable.ic_outline_thumb_down_24, "点踩", "点踩"),
            RoundButtonData(R.drawable.ic_baseline_history_24, "稍后再看", "稍后再看"),
            RoundButtonData(R.drawable.send_to_mobile, "手机观看", "手机观看"),
            RoundButtonData(R.drawable.cloud_download, "缓存", "缓存")
        )
    )*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //_binding = FragmentVideoInfoBinding.inflate(inflater, container, false)
        return ComposeView(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity = getActivity() as BangumiActivity
        activity.viewModel.bangumi.observe(viewLifecycleOwner) {
            if (it.result.episodes.isNotEmpty()) {
                VideoManager.uploadVideoViewingProgress(
                    it.result.episodes[0].bvid,
                    it.result.episodes[0].cid,
                    0
                )
            }
        }
        activity.viewModel.currentEpid =
            if (activity.idType == ID_TYPE_EPID && activity.id.isNotEmpty()) activity.id.toLong() else 0L
        (view as ComposeView).setContent {
            val bangumi by activity.viewModel.bangumi.observeAsState()
            Crossfade(targetState = bangumi != null) {
                if (it) {
                    MainUI(bangumi)
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
                                painter = painterResource(id = R.drawable.loading_2233),
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

    @Composable
    fun MainUI(bangumi: BangumiDetail?) {
        val localDensity = LocalDensity.current
        /*var followTextHeight by remember {
            mutableStateOf(0.dp)
        }*/
        var descriptionMaxLines by remember {
            mutableStateOf(3)
        }
        var totalWidth by remember {
            mutableStateOf(0.dp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = activity.viewModel.scrollState)
                .onGloballyPositioned {
                    totalWidth = with(localDensity) {
                        it.size.width.toDp()
                    }
                }
            /*.nestedScroll(connection = object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    return available
                }
            })*/
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier
                        .weight(2f)
                        //.fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .clickVfx {
                            val intent =
                                Intent(requireActivity(), PhotoViewActivity::class.java)
                            intent.putExtra("imageUrl", bangumi?.result?.cover)
                            startActivity(intent)
                        }
                        .clip(RoundedCornerShape(10.dp))) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(bangumi?.result?.cover)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(0.75f, matchHeightConstraintsFirst = true)
                        )   //番剧封面
                        if (bangumi?.result?.rating?.score != null) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(249, 157, 87, 255),
                                            fontFamily = googleSansFamily,
                                            fontSize = 10.sp
                                        )
                                    ) {
                                        append(bangumi.result.rating.score.toString())
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(249, 157, 87, 255),
                                            fontFamily = puhuiFamily,
                                            fontSize = 8.sp
                                        )
                                    ) {
                                        append("分")
                                    }
                                },
                                color = Color.White,
                                fontSize = 7.sp,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .align(BottomStart)
                                    .clip(
                                        RoundedCornerShape(topEnd = 12.dp)
                                    )
                                    .background(
                                        Color(67, 67, 67, 255)
                                    )
                                    .padding(
                                        start = 7.dp,
                                        end = 7.dp,
                                        top = 2.dp,
                                        bottom = 2.dp
                                    )
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = bangumi?.result?.title ?: "",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        if (!bangumi?.result?.areas.isNullOrEmpty()) {
                            Text(
                                text = "${bangumi?.result?.areas?.get(0)?.name}, ${bangumi?.result?.new_ep?.desc}",
                                fontFamily = puhuiFamily,
                                color = Color.Gray, fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        //TODO 追番
                        /*Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(360.dp))
                                .background(
                                    BilibiliPink
                                )
                                .padding(start = 6.dp, end = 10.dp, top = 4.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(followTextHeight)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "追番",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                modifier = Modifier.onGloballyPositioned {
                                    followTextHeight = with(localDensity) {
                                        it.size.height.toDp()
                                    }
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }*/
                    }   //番剧信息
                }   //番剧基本信息
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bangumi?.result?.evaluate ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween(durationMillis = 400))
                        .clickable {
                            descriptionMaxLines = if (descriptionMaxLines == 3) Int.MAX_VALUE else 3
                        },
                    fontSize = 9.sp,
                    color = Color.White,
                    fontFamily = puhuiFamily,
                    maxLines = descriptionMaxLines,
                    overflow = TextOverflow.Ellipsis
                )

                //简介
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isRound()) {
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.views?.toShortChinese() ?: "0"}播放",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.favorites?.toShortChinese() ?: "0"}追番",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_danmaku),
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.danmakus?.toShortChinese() ?: "0"}弹幕",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.Movie,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = bangumi?.result?.episodes?.get(0)?.bvid ?: "",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                    } else {
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.views?.toShortChinese() ?: "0"}播放",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.favorites?.toShortChinese() ?: "0"}追番",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_danmaku),
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${bangumi?.result?.stat?.danmakus?.toShortChinese() ?: "0"}弹幕",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.Movie,
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .size(textHeight), tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (bangumi?.result?.episodes.isNullOrEmpty()) "" else bangumi?.result?.episodes?.get(
                                    0
                                )?.bvid ?: "",
                                fontSize = 10.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                    }
                }   //番剧流量信息
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!bangumi?.result?.episodes.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    var textHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Text(
                        text = "选集(${bangumi?.result?.episodes?.size ?: 0})",
                        fontFamily = puhuiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.onGloballyPositioned {
                            textHeight = with(localDensity) {
                                it.size.height.toDp()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(textHeight.times(0.6f))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    var rowHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    val episodeScrollState = rememberLazyListState()
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                rowHeight = with(localDensity) { it.size.height.toDp() }
                            },
                        state = episodeScrollState,
                        contentPadding = PaddingValues(start = 10.dp, end = 2.dp)
                    ) {
                        bangumi?.result?.episodes?.forEachIndexed { index, episode ->
                            item {
                                val borderColor by animateColorAsState(
                                    targetValue = if (activity.viewModel.currentCid == episode.cid) Color(
                                        254,
                                        103,
                                        154,
                                        112
                                    ) else Color(112, 112, 112)
                                )
                                val textColor by animateColorAsState(targetValue = if (activity.viewModel.currentCid == episode.cid) BilibiliPink else Color.White)
                                Column(
                                    modifier = Modifier
                                        .requiredSizeIn(maxWidth = totalWidth.div(1.5f))
                                        .clickVfx {
                                            VideoManager.uploadVideoViewingProgress(
                                                episode.bvid,
                                                episode.cid,
                                                0
                                            )
                                            activity.viewModel.currentAid.value = episode.aid
                                            activity.viewModel.currentCid =
                                                if (activity.viewModel.currentCid == episode.cid) 0 else episode.cid
                                            activity.viewModel.currentBvid =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else episode.bvid
                                            activity.viewModel.currentCover =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else episode.cover
                                            activity.viewModel.currentEpid =
                                                if (activity.viewModel.currentEpid == episode.id) 0L else episode.id
                                            activity.viewModel.currentTitle =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else "EP${
                                                    index.plus(1)
                                                } ${if (episode.long_title.isNullOrEmpty()) episode.title else episode.long_title}"
                                        }
                                        .border(
                                            width = (0.1).dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(36, 36, 36, 166))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "EP${index.plus(1)}",
                                            fontFamily = googleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            fontSize = 11.sp
                                        )
                                        if (!episode.badge.isNullOrEmpty() && !episode.badge_info?.bg_color.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = episode.badge,
                                                color = Color.White,
                                                fontSize = 7.sp,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier
                                                    .offset(y = (-0.4).dp)
                                                    .clip(
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .background(
                                                        parseColor(
                                                            episode.badge_info?.bg_color
                                                                ?: "#FFFFFF"
                                                        )
                                                    )
                                                    .padding(
                                                        start = 5.dp,
                                                        end = 5.dp,
                                                        top = 2.dp,
                                                        bottom = 2.dp
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    (if (episode.long_title.isNullOrEmpty()) episode.title else episode.long_title)?.let {
                                        Text(
                                            text = it,
                                            fontFamily = puhuiFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .sizeIn(minHeight = with(LocalDensity.current) {
                                                    (9.sp * 3).toDp()
                                                })
                                                .alpha(0.8f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                LaunchedEffect(
                                    key1 = activity.viewModel.currentEpid == episode.id,
                                    block = {
                                        if (activity.viewModel.currentEpid == episode.id) {
                                            activity.viewModel.currentAid.value = episode.aid
                                            activity.viewModel.currentCid = episode.cid
                                            activity.viewModel.currentBvid = episode.bvid
                                            activity.viewModel.currentCover = episode.cover
                                            activity.viewModel.currentTitle = "EP${
                                                index.plus(1)
                                            } ${if (episode.long_title.isNullOrEmpty()) episode.title else episode.long_title}"
                                        }
                                    })
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                    /*Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .alpha(episodeScrollState. / sixteenDpsToPx)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0, 0, 0, 204),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .height(rowHeight)
                                .width(32.dp),
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .alpha(1f)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0, 0, 0, 204),

                                            )
                                    )
                                )
                                .height(rowHeight)
                                .width(32.dp),
                        )
                    }*/
                }
            }

            bangumi?.result?.section?.forEach { section ->
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    var textHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Text(
                        text = "${section.title}(${section.episodes.size})",
                        fontFamily = puhuiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.onGloballyPositioned {
                            textHeight = with(localDensity) {
                                it.size.height.toDp()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(textHeight.times(0.6f))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    var rowHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    val episodeScrollState = rememberLazyListState()
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                rowHeight = with(localDensity) { it.size.height.toDp() }
                            },
                        state = episodeScrollState,
                        contentPadding = PaddingValues(start = 10.dp, end = 2.dp)
                    ) {
                        section.episodes.forEachIndexed { index, episode ->
                            item {
                                val borderColor by animateColorAsState(
                                    targetValue = if (activity.viewModel.currentCid == episode.cid) Color(
                                        254,
                                        103,
                                        154,
                                        112
                                    ) else Color(112, 112, 112)
                                )
                                val textColor by animateColorAsState(targetValue = if (activity.viewModel.currentCid == episode.cid) BilibiliPink else Color.White)
                                LaunchedEffect(
                                    key1 = activity.viewModel.currentEpid == episode.id,
                                    block = {
                                        if (activity.viewModel.currentEpid == episode.id) {
                                            activity.viewModel.currentAid.value = episode.aid
                                            activity.viewModel.currentCid = episode.cid
                                            activity.viewModel.currentBvid = episode.bvid
                                            activity.viewModel.currentCover = episode.cover
                                            activity.viewModel.currentTitle =
                                                "P${index.plus(1)} ${episode.title} ${episode.long_title}"
                                        }
                                    })
                                Column(
                                    modifier = Modifier
                                        .requiredSizeIn(maxWidth = totalWidth.div(1.5f))
                                        .clickVfx {
                                            VideoManager.uploadVideoViewingProgress(
                                                episode.bvid,
                                                episode.cid,
                                                0
                                            )
                                            activity.viewModel.currentAid.value = episode.aid
                                            activity.viewModel.currentCid =
                                                if (activity.viewModel.currentCid == episode.cid) 0 else episode.cid
                                            activity.viewModel.currentBvid =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else episode.bvid
                                            activity.viewModel.currentEpid =
                                                if (activity.viewModel.currentEpid == episode.id) 0L else episode.id
                                            activity.viewModel.currentCover =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else episode.cover
                                            activity.viewModel.currentTitle =
                                                if (activity.viewModel.currentBvid == episode.bvid) "" else "P${
                                                    index.plus(
                                                        1
                                                    )
                                                } ${episode.title} ${episode.long_title}"
                                        }
                                        .border(
                                            width = (0.1).dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(36, 36, 36, 166))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "P${index.plus(1)}",
                                            fontFamily = googleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            fontSize = 11.sp
                                        )
                                        if (!episode.badge.isNullOrEmpty() && !episode.badge_info?.bg_color.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = episode.badge,
                                                color = Color.White,
                                                fontSize = 7.sp,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier
                                                    .offset(y = (-0.4).dp)
                                                    .clip(
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .background(
                                                        parseColor(
                                                            episode.badge_info?.bg_color
                                                                ?: "#FFFFFF"
                                                        )
                                                    )
                                                    .padding(
                                                        start = 5.dp,
                                                        end = 5.dp,
                                                        top = 2.dp,
                                                        bottom = 2.dp
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${episode.title} ${episode.long_title}",
                                        fontFamily = puhuiFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .sizeIn(minHeight = with(LocalDensity.current) {
                                                (9.sp * 3).toDp()
                                            })
                                            .alpha(0.8f),
                                        fontSize = 9.sp
                                    )

                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                    /*Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .alpha(episodeScrollState. / sixteenDpsToPx)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0, 0, 0, 204),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .height(rowHeight)
                                .width(32.dp),
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .alpha(1f)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0, 0, 0, 204),

                                            )
                                    )
                                )
                                .height(rowHeight)
                                .width(32.dp),
                        )
                    }*/
                }
            }   //其他部分视频
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.PlayCircle,
                            "播放",
                            "播放"
                        ), onLongClick = {
                            Log.d(
                                Application.TAG,
                                "setOnLongClickListener: "
                            )
                            val intent =
                                Intent(
                                    context,
                                    ChooseSettingsActivity::class.java
                                )
                            val item =
                                SettingsManager.getSettingByName("defaultPlayer")
                            intent.putExtra("item", item)
                            /*intent.putExtra("itemKey", item?.settingName)
                            intent.putExtra("itemName", item?.displayName)
                            intent.putExtra("defVal", item?.defString)*/
                            startActivity(intent)
                        }, tint = Color.White, modifier = Modifier.weight(1f)
                    ) {
                        if (activity.viewModel.currentBvid.isNotEmpty() && activity.viewModel.currentCid != 0L) {
                            SettingsManager.playVideo(
                                context = requireContext(),
                                bvid = activity.viewModel.currentBvid,
                                cid = activity.viewModel.currentCid,
                                title = activity.viewModel.currentTitle,
                                progress = 0L,
                                subtitleUrl = null
                            )
                        }

                    }
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.ThumbUp,
                            "点赞",
                            "点赞"
                        ),
                        onLongClick = { },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {

                    }
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.MonetizationOn,
                            "投币",
                            "投币"
                        ),
                        onLongClick = { },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {

                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.StarBorder,
                            "收藏",
                            "收藏"
                        ),
                        onLongClick = { },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {

                    }
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.History,
                            "稍后再看",
                            "稍后再看"
                        ),
                        onLongClick = { },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {

                    }
                    RoundButton(
                        buttonItem = RoundButtonDataNew(
                            Icons.Outlined.SendToMobile,
                            "手机观看",
                            "手机观看"
                        ),
                        onLongClick = { /*TODO*/ },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isAdded) {
                            if (activity.viewModel.currentEpid != 0L) {
                                val intent = Intent(
                                    requireActivity(),
                                    PlayOnPhoneActivity::class.java
                                )
                                intent.putExtra(
                                    "qrCodeUrl",
                                    "https://www.bilibili.com/bangumi/play/ep$activity.viewModel.currentEpid"
                                )
                                startActivity(intent)
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    RoundButton(
                        buttonItem = RoundButtonData(
                            R.drawable.cloud_download,
                            "缓存",
                            "缓存"
                        ),
                        onLongClick = { },
                        tint = Color.White,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (activity.viewModel.currentBvid.isNotEmpty() && activity.viewModel.currentCid != 0L && activity.viewModel.currentCover.isNotEmpty())
                            ExoPlayerUtils.getInstance(requireContext())
                                .downloadVideo(
                                    coverUrl = activity.viewModel.currentCover,
                                    title = activity.viewModel.currentTitle,
                                    partName = bangumi?.result?.title ?: "",
                                    bvid = activity.viewModel.currentBvid,
                                    cid = activity.viewModel.currentCid,
                                    subtitleUrl = null,
                                    onTaskAdded = {}
                                )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height((if (isRound()) 50 else 10).dp))
        }
    }
}