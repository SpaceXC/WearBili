package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import android.util.Log
import android.view.TextureView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.googleSansFamily
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
import cn.spacexc.wearbili.viewmodel.VideoPlayerViewModelV2
import com.google.android.exoplayer2.ExoPlayer
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.loader.ILoader
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.IDataSource
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser
import master.flame.danmaku.ui.widget.DanmakuView

/**
 * Created by XC-Qan on 2023/1/21.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

/**
 * 这儿就尽量别整动画了，省的拖慢性能
 */

class VideoPlayerV2 : AppCompatActivity() {
    val viewModel by viewModels<VideoPlayerViewModelV2>()
    var danmakuView: DanmakuView? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bvid = intent.getStringExtra("videoBvid") ?: ""
        val cid = intent.getLongExtra("videoCid", 0L)
        val title = intent.getStringExtra("videoTitle") ?: ""
        viewModel.apply {
            getVideoPartsList(bvid)
            uploadVideoProgress(bvid, cid)
            getOnlineCount(bvid, cid)
        }
        setContent {
            val loadingState by viewModel.loadingStateText
            val isLoading by viewModel.isLoading
            val playerSize by viewModel.videoResolution
            val isControlPanelVisible by viewModel.isControlPanelVisible
            val isPlaying by viewModel.isPlaying
            val playerPosition by viewModel.videoProgress
            val onlineCount by viewModel.onlineCount
            var isDraggingProgress by remember { mutableStateOf(false) }
            var currentProgress by remember { mutableStateOf(0f) }
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                        viewModel.toggleControlPanelVisibility()
                    }) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    AndroidView(
                        factory = { context -> TextureView(context) }, modifier = Modifier
                            .aspectRatio(playerSize.first.toFloat() / playerSize.second.toFloat())
                            .align(Alignment.Center)
                    ) { textureView ->
                        viewModel.videoPlayer.setVideoTextureView(textureView)
                    }   //视频显示TextureView
                }   //不在外面套一个居不了中，不知道为什么
                AndroidView(
                    factory = { context -> DanmakuView(context) },
                    modifier = Modifier.fillMaxSize()
                ) { danmakuView ->
                    this@VideoPlayerV2.danmakuView = danmakuView
                }
                if (isLoading) {
                    Text(
                        text = loadingState,
                        fontSize = 9.sp,
                        fontFamily = puhuiFamily,
                        color = Color.White,
                        modifier = Modifier
                            .alpha(0.6f)
                            .align(
                                Alignment.BottomStart
                            )
                            .padding(6.dp)
                    )
                }   //加载状态信息文字描述
                if (isControlPanelVisible && !isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(
                                                0,
                                                0,
                                                0,
                                                230
                                            ), Color.Transparent
                                        )
                                    )
                                )
                                .padding(horizontal = 8.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Column(modifier = Modifier
                                .padding(4.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) { finish() }) {
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${onlineCount}人在看",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontSize = 8.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null
                            ) {
                                viewModel.toggleControlPanelVisibility()
                            })
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(
                                                0,
                                                0,
                                                0,
                                                230
                                            )
                                        )
                                    )
                                )
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                        ) {
                            val interactionSource = MutableInteractionSource()
                            val isPressed by interactionSource.collectIsPressedAsState()
                            LaunchedEffect(key1 = isPressed, block = {
                                if (isPressed) {
                                    if (viewModel.playerStat.value == ExoPlayer.STATE_READY || viewModel.playerStat.value == ExoPlayer.STATE_BUFFERING) {
                                        viewModel.videoPlayer.pause()
                                        danmakuView?.pause()
                                    }
                                } else {
                                    if (viewModel.playerStat.value == ExoPlayer.STATE_READY || viewModel.playerStat.value == ExoPlayer.STATE_BUFFERING) {
                                        viewModel.videoPlayer.play()
                                        danmakuView?.resume()
                                    }
                                }
                            })

                            Text(
                                text = "${
                                    if (isDraggingProgress)
                                        currentProgress.div(1000).toInt().secondToTime()
                                    else
                                        playerPosition.div(1000).secondToTime()
                                }/${viewModel.videoPlayer.duration.div(1000).secondToTime()}",
                                fontFamily = googleSansFamily,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .offset(y = 16.dp)
                            )
                            androidx.compose.material3.Slider(
                                value = if (isDraggingProgress) currentProgress else playerPosition.toFloat(),
                                onValueChange = {
                                    isDraggingProgress = true
                                    currentProgress = it
                                },
                                interactionSource = MutableInteractionSource(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 8.dp),
                                valueRange = 0f..(if (viewModel.videoPlayer.duration < 0) 0 else viewModel.videoPlayer.duration).toFloat(),
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = BilibiliPink
                                ),
                                thumb = {
                                    androidx.compose.material3.SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
                                        interactionSource = interactionSource,
                                        thumbSize = DpSize(16.dp, 16.dp),
                                        colors = androidx.compose.material3.SliderDefaults.colors(
                                            thumbColor = Color.White
                                        ),
                                        modifier = Modifier.offset(y = 2.2.dp)
                                    )
                                },
                                onValueChangeFinished = {
                                    viewModel.videoPlayer.seekTo(currentProgress.toLong())
                                    isDraggingProgress = false
                                    viewModel.videoPlayer.play()
                                    viewModel.updateVideoPlayingProgress(bvid, cid)
                                },
                            )
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isPlaying) R.drawable.round_pause_black else R.drawable.play),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickVfx(onClick = viewModel::togglePlayerPlayingState),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(
                                            CircleShape
                                        )
                                        .background(Color(54, 54, 54, 255))
                                        .padding(4.dp),
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }   //视频播放控制，只能在「非加载时」显示
            }
        }
        //----------弹幕-------------
        viewModel.appendLoadingState("正在加载弹幕")
        val danmakuContext: DanmakuContext = DanmakuContext.create()       //弹幕context

        val maxLinesPair = HashMap<Int, Int>()     //弹幕最多行数
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_LR] = 5

        val overlappingEnablePair = HashMap<Int, Boolean>()     //防弹幕重叠
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_LR] = true
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_BOTTOM] = true

        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_DEFAULT) //设置默认样式
            .setDuplicateMergingEnabled(true)      //合并重复弹幕
            .setScrollSpeedFactor(1.2f)     //弹幕速度
            .setScaleTextSize(0.6f)     //文字大小
            .setCacheStuffer(SpannedCacheStuffer()) // 图文混排使用SpannedCacheStuffer  设置缓存绘制填充器，默认使用{@link SimpleTextCacheStuffer}只支持纯文字显示, 如果需要图文混排请设置{@link SpannedCacheStuffer}如果需要定制其他样式请扩展{@link SimpleTextCacheStuffer}|{@link SpannedCacheStuffer}
            .setMaximumLines(maxLinesPair) //设置最大显示行数
            .preventOverlapping(overlappingEnablePair) //设置防弹幕重叠，null为允许重叠

        val loader: ILoader =
            DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)       //设置解析b站xml弹幕
        viewModel.getVideoDanmaku(cid) { inputStream ->
            Log.d(TAG, "onCreate: ${String(inputStream.readBytes())}")
            loader.load(inputStream)
            val danmakuParser = BiliDanmukuParser()
            val dataSource: IDataSource<*> = loader.dataSource
            danmakuParser.load(dataSource)
            danmakuView?.setCallback(object : DrawHandler.Callback {
                override fun prepared() {
                    viewModel.appendLoadingState("弹幕载入完毕")
                    viewModel.getHttpVideoUrl(bvid = bvid, cid = cid)
                }

                override fun updateTimer(timer: DanmakuTimer?) {
                    timer?.currMillisecond.log()
                }

                override fun drawingFinished() {

                }

            })
            danmakuView?.prepare(danmakuParser, danmakuContext)
            danmakuView?.enableDanmakuDrawingCache(true)
            danmakuView?.showFPS(true)
        }
        danmakuView?.start()
        viewModel.isDanmakuPlaying.observe(this) {
            if (it) danmakuView?.start(viewModel.videoPlayer.currentPosition) else danmakuView?.pause()
        }
        viewModel.danmakuPosition.observe(this) {
            if (it > 0) danmakuView?.seekTo(it)
        }
        //----------弹幕-------------
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.videoPlayer.release()
        danmakuView?.release()
    }
}