package cn.spacexc.wearbili.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.video.MinifyVideoPlayer
import cn.spacexc.wearbili.activity.video.VideoPlayerActivity
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.ToastUtils
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
        (view as ComposeView).setContent {
            MainUI()
        }
    }

    @Composable
    fun MainUI() {
        val bangumi by activity.viewModel.bangumi.observeAsState()
        val localDensity = LocalDensity.current
        var followTextHeight by remember {
            mutableStateOf(0.dp)
        }
        var descriptionMaxLines by remember {
            mutableStateOf(3)
        }
        val state = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .verticalScroll(state = state)
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
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bangumi?.result?.cover)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .weight(2f)
                        //.fillMaxHeight()
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .align(Alignment.CenterVertically), contentDescription = null
                )   //番剧封面
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${bangumi?.result?.areas?.get(0)?.name}, ${bangumi?.result?.new_ep?.desc}",
                        fontFamily = puhuiFamily,
                        color = Color.Gray, fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
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
                            fontSize = 12.sp,
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            modifier = Modifier.onGloballyPositioned {
                                followTextHeight = with(localDensity) {
                                    it.size.height.toDp()
                                }
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
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
                fontSize = 13.sp,
                color = Color.White,
                fontFamily = puhuiFamily,
                maxLines = descriptionMaxLines,
                overflow = TextOverflow.Ellipsis
            )

            //简介
            Spacer(modifier = Modifier.height(4.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                if (LocalConfiguration.current.isScreenRound) {
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
                    //TODO 调整下
                    /*Row(modifier = Modifier.fillMaxWidth()) {
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
                                fontSize = 12.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
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
                                fontSize = 12.sp,
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
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
                                fontSize = 12.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
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
                                fontSize = 12.sp,
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .onGloballyPositioned {
                                        textHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                            )
                        }
                    }*/
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
                            fontSize = 12.sp,
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
            Text(
                text = "选集(${bangumi?.result?.episodes?.size ?: 0})",
                fontFamily = puhuiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = Color.White
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!bangumi?.result?.episodes.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredSizeIn(maxHeight = 140.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            bangumi?.result?.episodes?.forEachIndexed { index, episode ->
                                item {
                                    Column(modifier = Modifier.clickable {
                                        playVideo(
                                            bvid = episode.bvid,
                                            cid = episode.cid,
                                            title = "EP${index + 1} ${if (episode.long_title.isNullOrEmpty()) episode.title else episode.long_title}"
                                        )
                                    }) {
                                        Column(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(
                                                    width = 0.1f.dp, color = Color(
                                                        112,
                                                        112,
                                                        112,
                                                        204
                                                    ), shape = RoundedCornerShape(10.dp)
                                                )

                                                .background(color = Color(36, 36, 36, 199))
                                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = "EP${index + 1} ${if (episode.long_title.isNullOrEmpty()) episode.title else episode.long_title}",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontFamily = puhuiFamily,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.alpha(0.76f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }


                                }
                            }
                        }   //部分选集
                        if ((bangumi?.result?.episodes?.size ?: 0) > 3) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0, 0, 0, 204)
                                            )
                                        )
                                    )
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.BottomCenter),
                                color = Color.Transparent,
                                content = {})   //阴影
                        }
                    }   //部分选集
                }

                bangumi?.result?.section?.forEach { section ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${section.title}(${section.episodes.size})",
                        fontFamily = puhuiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                //.height(90.dp)
                                .requiredSizeIn(maxHeight = 140.dp)
                        ) {
                            section.episodes.forEach { episode ->
                                item {
                                    Column(modifier = Modifier.clickable {
                                        playVideo(
                                            bvid = episode.bvid,
                                            cid = episode.cid,
                                            title = "${episode.title} ${episode.long_title}"
                                        )
                                    }) {
                                        Column(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(
                                                    width = 0.1f.dp, color = Color(
                                                        112,
                                                        112,
                                                        112,
                                                        204
                                                    ), shape = RoundedCornerShape(10.dp)
                                                )

                                                .background(color = Color(36, 36, 36, 199))
                                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = "${episode.title} ${episode.long_title}",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontFamily = puhuiFamily,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.alpha(0.76f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }   //部分选集
                        if (section.episodes.size > 3) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0, 0, 0, 204)
                                            )
                                        )
                                    )
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.BottomCenter),
                                color = Color.Transparent,
                                content = {})   //阴影
                        }

                    }   //部分选集
                }   //其他部分视频
            }
            Spacer(modifier = Modifier.height((if (LocalConfiguration.current.isScreenRound) 50 else 10).dp))
        }
    }

    private fun playVideo(bvid: String, cid: Long, title: String) {
        when (SettingsManager.defPlayer()) {
            "builtinPlayer" -> {
                val intent =
                    Intent(requireActivity(), VideoPlayerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("videoBvid", bvid)
                intent.putExtra("videoCid", cid)
                intent.putExtra("videoTitle", title)
                intent.putExtra("progress", 0)
                startActivity(intent)
            }
            "minifyPlayer" -> {
                val intent =
                    Intent(requireActivity(), MinifyVideoPlayer::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("videoBvid", bvid)
                intent.putExtra("videoCid", cid)
                intent.putExtra("videoTitle", title)
                startActivity(intent)
            }
            "microTvPlayer" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("wearbiliplayer://receive:8080/play?&bvid=$bvid&cid=$cid&aid=0")
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    ToastUtils.makeText("需要安装小电视播放器哦").show()
                }
            }
            "microTaiwan" -> {
                ToastUtils.showText("敬请期待")
            }
            "other" -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("wearbili-3rd://video/play?&bvid=$bvid&cid=$cid")
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    ToastUtils.showText("没有找到其他播放器哦")
                }
            }
        }
    }
}