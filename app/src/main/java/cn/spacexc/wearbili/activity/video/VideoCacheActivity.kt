package cn.spacexc.wearbili.activity.video

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.viewmodel.DownloadVideoInfoViewModel
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadService

class VideoCacheActivity : AppCompatActivity() {
    val viewModel by viewModels<DownloadVideoInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "离线缓存",
                onBack = ::finish
            ) {
                val videos by viewModel.downloads.observeAsState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    videos?.forEach {
                        item {
                            Column(modifier = Modifier) {
                                Column(
                                    modifier = Modifier
                                        .clickable {
                                            when (it.state) {
                                                Download.STATE_COMPLETED -> {
                                                    Intent(
                                                        this@VideoCacheActivity,
                                                        VideoPlayerActivity::class.java
                                                    ).apply {
                                                        putExtra("videoCid", it.request.id)
                                                        putExtra("isCache", true)
                                                        startActivity(this)
                                                    }
                                                }
                                                Download.STATE_FAILED -> {
                                                    //DownloadService.sendAddDownload(Application.context!!, cn.spacexc.wearbili.service.DownloadService::class.java, it.request, false)
                                                    DownloadService.sendRemoveDownload(
                                                        Application.context!!,
                                                        cn.spacexc.wearbili.service.DownloadService::class.java,
                                                        it.request.id,
                                                        false
                                                    )
                                                    viewModel.getDownload()
                                                }
                                                Download.STATE_DOWNLOADING -> {

                                                }
                                                Download.STATE_QUEUED -> {

                                                }
                                                Download.STATE_REMOVING -> {

                                                }
                                                Download.STATE_RESTARTING -> {

                                                }
                                                Download.STATE_STOPPED -> {
                                                    DownloadService.sendResumeDownloads(
                                                        Application.context!!,
                                                        cn.spacexc.wearbili.service.DownloadService::class.java,
                                                        false
                                                    )
                                                    viewModel.getDownload()
                                                }
                                            }
                                        }
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
                                        text = it.request.id,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontFamily = puhuiFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.alpha(0.76f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = it.startTimeMs.toDateStr(),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = puhuiFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.alpha(0.76f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "状态：${
                                            when (it.state) {
                                                Download.STATE_QUEUED -> "等待中"
                                                Download.STATE_COMPLETED -> "已完成"
                                                Download.STATE_FAILED -> "下载失败"
                                                Download.STATE_DOWNLOADING -> "下载中"
                                                else -> "未知"
                                            }
                                        }",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = puhuiFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.alpha(0.76f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (it.state == Download.STATE_FAILED) {
                                        Text(
                                            text = "错误码：${it.failureReason}",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = puhuiFamily,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.alpha(0.76f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = "进度： ${it.percentDownloaded.toInt()}%",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = puhuiFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.alpha(0.76f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    LinearProgressIndicator(
                                        progress = it.percentDownloaded / 100f,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = BilibiliPink,
                                        trackColor = Color(
                                            254,
                                            103,
                                            154,
                                            128
                                        )
                                    )

                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}