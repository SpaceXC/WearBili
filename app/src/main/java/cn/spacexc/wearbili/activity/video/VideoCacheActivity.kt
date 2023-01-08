package cn.spacexc.wearbili.activity.video

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.CoverSharedPreferencesUtils
import cn.spacexc.wearbili.utils.DanmakuSharedPreferencesUtils
import cn.spacexc.wearbili.utils.SubtitleSharedPreferencesUtils
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.viewmodel.DownloadVideoInfoViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadService
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

class VideoCacheActivity : AppCompatActivity() {
    val viewModel by viewModels<DownloadVideoInfoViewModel>()

    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalWearMaterialApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val downloads by viewModel.downloads.observeAsState()
            val downloadings by viewModel.downloadings.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "离线缓存",
                onBack = ::finish
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        bottom = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                ) {
                    if (!downloadings.isNullOrEmpty()) {
                        item {
                            Text(
                                text = "正在下载",
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        downloadings?.forEach {
                            item(key = it.request.id) {
                                val state = rememberDismissState()
                                SwipeToDismiss(
                                    state = state,
                                    background = {},
                                    directions = setOf(/*DismissDirection.EndToStart*/)
                                ) {
                                    CacheCard(
                                        cover = CoverSharedPreferencesUtils.getUrl(
                                            it.request.id.split("///")[0]
                                        ) ?: "",
                                        title = it.request.id.split("///")[1],
                                        partName = it.request.id.split("///")[2],
                                        time = it.startTimeMs.toDateStr(),
                                        cacheId = it.request.id,
                                        state = it.state,
                                        progress = it.percentDownloaded.toInt(),
                                        downloadedSize = it.bytesDownloaded,
                                        modifier = Modifier.animateItemPlacement()
                                    )
                                }
                            }
                        }
                    }
                    if (!downloads.isNullOrEmpty()) {
                        item {
                            Text(
                                text = "下载完成",
                                fontFamily = puhuiFamily,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        downloads?.asReversed()?.forEach {
                            item(key = it.request.id) {
                                val state = rememberDismissState()
                                if (state.isDismissed(DismissDirection.EndToStart)) {
                                    val cid = it.request.id.split("///")[0]
                                    DownloadService.sendRemoveDownload(
                                        application,
                                        cn.spacexc.wearbili.service.DownloadService::class.java,
                                        it.request.id,
                                        false
                                    )
                                    if (DanmakuSharedPreferencesUtils.contains(cid)) {
                                        File(DanmakuSharedPreferencesUtils.getUrl(cid)!!).delete()
                                    }
                                    if (CoverSharedPreferencesUtils.contains(cid)) {
                                        File(CoverSharedPreferencesUtils.getUrl(cid)!!).delete()
                                    }
                                    if (SubtitleSharedPreferencesUtils.contains(cid)) {
                                        File(SubtitleSharedPreferencesUtils.getUrl(cid)!!).delete()
                                    }
                                }
                                SwipeToDismiss(
                                    state = state, background = {

                                    }/*, dismissThresholds = { direction ->
                                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.25f else 0.5f)
                                }*/, directions = setOf(DismissDirection.EndToStart)
                                ) {
                                    Box(modifier = Modifier.animateItemPlacement()) {
                                        CacheCard(
                                            cover = CoverSharedPreferencesUtils.getUrl(
                                                it.request.id.split("///")[0]
                                            ) ?: "",
                                            title = it.request.id.split("///")[1],
                                            partName = it.request.id.split("///")[2],
                                            time = it.startTimeMs.toDateStr(),
                                            cacheId = it.request.id,
                                            state = it.state,
                                            progress = it.percentDownloaded.toInt(),
                                            downloadedSize = it.bytesDownloaded,
                                            //modifier = Modifier.animateItemPlacement()
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

    @Composable
    fun CacheCard(
        //isCompleted: Boolean,
        cover: String,
        title: String,
        partName: String,
        time: String,
        progress: Int,
        cacheId: String,
        state: Int,
        downloadedSize: Long,
        modifier: Modifier = Modifier
    ) {
        var iconHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        val progressVal by animateFloatAsState(
            targetValue = progress.toFloat() / 100,
            animationSpec = tween(durationMillis = 300)
        )
        Column(modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {

                }, onTap = {
                    if (state == Download.STATE_COMPLETED) {
                        Intent(
                            this@VideoCacheActivity,
                            VideoPlayerActivity::class.java
                        ).apply {
                            putExtra("videoCid", cacheId)
                            putExtra("isCache", true)
                            startActivity(this)
                        }
                    }
                })
            })
        {
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(color = Color(36, 36, 36, 100))
                    .padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Spacer(Modifier.width(4.dp))
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            if (cover.isEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(R.drawable.placeholder)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.3f, matchHeightConstraintsFirst = false)
                                        .padding(end = 2.dp)
                                        //.align(Alignment.CenterVertically)
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .offset(y = (1f).dp),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(File(cover))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.3f, matchHeightConstraintsFirst = false)
                                        .padding(end = 2.dp)
                                        //.align(Alignment.CenterVertically)
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .offset(y = (1f).dp),
                                    contentScale = ContentScale.Crop,
                                )
                            }

                        }

                        Spacer(Modifier.width(8.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = title,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Color.White,
                            maxLines = 3,
                            //modifier = Modifier.align(Alignment.CenterVertically),
                            overflow = TextOverflow.Ellipsis
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .width(iconHeight)
                                    .height(iconHeight),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(1.dp))
                            Text(
                                text = time,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .onGloballyPositioned {
                                        iconHeight = with(localDensity) {
                                            it.size.height.toDp()
                                        }
                                    },
                                fontFamily = puhuiFamily,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.VideoLibrary,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .width(iconHeight)
                                    .height(iconHeight),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(1.dp))
                            Text(
                                text = partName,
                                color = Color.White,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .onGloballyPositioned {
                                        iconHeight = with(localDensity) {
                                            it.size.height.toDp()
                                        }
                                    },
                                fontFamily = puhuiFamily,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .width(iconHeight)
                                    .height(iconHeight),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(1.dp))
                            Text(
                                text = byteConverter(
                                    downloadedSize,
                                    DanmakuSharedPreferencesUtils.getUrl(cacheId.split("///")[0])
                                        ?: "",
                                    cover
                                ), color = Color.White,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .onGloballyPositioned {
                                        iconHeight = with(localDensity) {
                                            it.size.height.toDp()
                                        }
                                    },
                                fontFamily = puhuiFamily,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                        //Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (state != Download.STATE_COMPLETED) {
                    Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
                        Text(
                            text = "${
                                when (state) {
                                    Download.STATE_QUEUED -> "等待中"
                                    Download.STATE_FAILED -> "下载失败"
                                    Download.STATE_DOWNLOADING -> "正在下载"
                                    else -> "未知"
                                }
                            } $progress%",
                            fontFamily = puhuiFamily,
                            fontSize = 12.sp,
                            color = if (state == Download.STATE_FAILED) BilibiliPink else Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = progressVal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(360.dp)),
                            color = BilibiliPink,
                            backgroundColor = Color(
                                254,
                                103,
                                154,
                                128
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }

    /**
     *  懒得自己写一个了
     *  上CSDN🇰🇷的
     */
    private fun byteConverter(videoSize: Long, danmakuUri: String, coverUri: String): String {
        try {
            val danmakuFile = File(danmakuUri)
            val picFile = File(coverUri)
            val bytes = danmakuFile.length() + picFile.length() + videoSize
            //获取到的size为：1705230
            val sizeGB = 1024 * 1024 * 1024 //定义GB的计算常量
            val sizeMB = 1024 * 1024 //定义MB的计算常量
            val sizeKB = 1024 //定义KB的计算常量
            val df = DecimalFormat("0.00") //格式化小数
            val resultSize: String = if (bytes / sizeGB >= 1) {
                //如果当前Byte的值大于等于1GB
                df.format(bytes / sizeGB.toFloat()) + "GB"
            } else if (bytes / sizeMB >= 1) {
                //如果当前Byte的值大于等于1MB
                df.format(bytes / sizeMB.toFloat()) + "MB"
            } else if (bytes / sizeKB >= 1) {
                //如果当前Byte的值大于等于1KB
                df.format(bytes / sizeKB.toFloat()) + "KB"
            } else {
                bytes.toString() + "B   "
            }
            return resultSize
        } catch (e: IOException) {
            return ""
        }
    }
}