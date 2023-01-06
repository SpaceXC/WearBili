package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.runtime.*
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Created by Xiaochang on 2022/9/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
object VideoUis {
    @Composable
    fun VideoCard(
        videoName: String,
        uploader: String,
        views: String,
        coverUrl: String,
        hasViews: Boolean = true,
        videoBvid: String = "",
        clickable: Boolean = true,
        isBangumi: Boolean = false,
        epid: String = "",
        ssid: String = "",
        badge: String = "",
        tagName: String = "",
        context: Context = Application.getContext()
    ) {
        var iconHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Column(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    if (videoBvid.isNotEmpty()) {
                        val intent = Intent(context, VideoLongClickActivity::class.java)
                        intent.putExtra("bvid", videoBvid)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }, onTap = {
                    if (isBangumi && epid.isNotEmpty()) {
                        Intent(context, BangumiActivity::class.java).apply {
                            if (epid.isNotEmpty()) {
                                putExtra("id", epid)
                                putExtra("idType", ID_TYPE_EPID)
                            } else {
                                putExtra("id", ssid)
                                putExtra("idType", ID_TYPE_SSID)
                            }
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (clickable && videoBvid.isNotEmpty()) {
                        Intent(context, VideoActivity::class.java).apply {
                            putExtra("videoId", videoBvid)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    }
                })
            }) {
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
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(coverUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.6f, matchHeightConstraintsFirst = false)
                                    //.align(Alignment.CenterVertically)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .offset(y = (1f).dp),
                                contentScale = ContentScale.Crop,
                            )
                            if (badge.isNotEmpty()) {
                                Text(
                                    text = badge,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    modifier = Modifier
                                        .padding(top = 4.dp, end = 4.dp)
                                        .clip(
                                            RoundedCornerShape(4.dp)
                                        )
                                        .background(
                                            BilibiliPink
                                        )
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                        .align(Alignment.TopEnd)
                                )
                            }

                        }

                        Spacer(Modifier.width(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = videoName,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Color.White,
                            maxLines = 3,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            overflow = TextOverflow.Ellipsis
                        )
                        //Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    if (hasViews) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.PlayCircleOutline,
                            modifier = Modifier
                                .alpha(0.5f)
                                .width(iconHeight)
                                .height(iconHeight),
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(1.dp))
                        Text(
                            text = views,
                            color = Color.White,
                            modifier = Modifier.alpha(0.5f),
                            fontFamily = puhuiFamily,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    if (uploader.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            modifier = Modifier
                                .alpha(0.5f)
                                .width(iconHeight)
                                .height(iconHeight),
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(1.dp))
                        Text(
                            text = uploader,
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
                }
            }
            Spacer(Modifier.height(4.dp))
        }

    }

    @Composable
    fun BangumiCard(
        bangumiName: String,
        cover: String,
        areaInfo: String,
        description: String,
        id: String,
        idType: String,
        context: Context = Application.getContext()
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickVfx {
                    Intent(
                        context,
                        BangumiActivity::class.java
                    ).apply {
                        putExtra("id", id)
                        putExtra("idType", idType)
                        context.startActivity(this)
                    }
                }
                .border(
                    width = 0.1f.dp,
                    color = Color(112, 112, 112, 70),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(color = Color(36, 36, 36, 100))
                .padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cover)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .weight(2f)
                        //.fillMaxHeight()
                        .fillMaxSize()
                        .aspectRatio(0.75f, matchHeightConstraintsFirst = true)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(10.dp)), contentDescription = null
                )   //番剧封面
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = bangumiName,
                        color = Color.White,
                        fontFamily = puhuiFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = areaInfo,
                        fontFamily = puhuiFamily,
                        color = Color.Gray, fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontFamily = puhuiFamily,
                        color = Color.Gray, fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }   //番剧信息
            }
        }
    }
}