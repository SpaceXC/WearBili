package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.SimplestUniversalDataClass
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.parseColor
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

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
        modifier: Modifier = Modifier,
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
        //tagName: String = "",
        context: Context = Application.getContext()
    ) {
        var iconHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Column(
            modifier = modifier
                .animateContentSize()
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
                    .animateContentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(color = Color(36, 36, 36, 100))
                    .padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp)
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
                                        RoundedCornerShape(6.dp)
                                    )
                                    .offset(y = (1f).dp),
                                contentScale = ContentScale.Crop,
                            )
                            if (badge.isNotEmpty()) {
                                Text(
                                    text = badge,
                                    color = Color.White,
                                    fontSize = 7.sp,
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
                            /*if (tagName.isNotEmpty()) {
                                Text(
                                    text = tagName,
                                    color = Color.White,
                                    fontSize = 7.sp,
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
                            }*/

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
                            fontSize = 11.sp,
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
                            fontSize = 8.sp,
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
                            fontSize = 8.sp,
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
    fun VideoCard(
        modifier: Modifier = Modifier,
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
        //tagName: String = "",
        expandHeight: Dp,
        isExpand: Boolean,
        onLongClickExpand: () -> Unit,
        onExpandBack: () -> Unit,
        context: Context = Application.getContext()
    ) {
        var iconHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Column(
            modifier = modifier
                .animateContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        onLongClickExpand()
                        /*if (videoBvid.isNotEmpty()) {
                            val intent = Intent(context, VideoLongClickActivity::class.java)
                            intent.putExtra("bvid", videoBvid)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }*/
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
                modifier = (if (isExpand && videoBvid.isNotEmpty()) Modifier.height(expandHeight) else Modifier)
                    .animateContentSize(animationSpec = tween(durationMillis = 400))
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(color = Color(36, 36, 36, 100))
                    .padding(
                        start = (if (isExpand) 0 else 6).dp,
                        end = (if (isExpand) 0 else 6).dp,
                        top = (if (isExpand) 6 else 10).dp,
                        bottom = (if (isExpand) 0 else 10).dp
                    )
            ) {
                Crossfade(targetState = isExpand, animationSpec = tween(durationMillis = 400)) {
                    if (it && videoBvid.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            var backButtonHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Column(modifier = Modifier.padding(bottom = backButtonHeight)) {
                                VideoOperationItem(
                                    icon = Icons.Outlined.History,
                                    content = "添加到稍后再看"
                                ) {
                                    VideoManager.addToView(videoBvid, object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            MainScope().launch {
                                                ToastUtils.makeText(
                                                    "网络异常"
                                                ).show()
                                                onExpandBack()
                                            }

                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            val result = Gson().fromJson(
                                                response.body?.string(),
                                                SimplestUniversalDataClass::class.java
                                            )
                                            MainScope().launch {
                                                when (result.code) {
                                                    0 -> {
                                                        ToastUtils.makeText(
                                                            "添加成功"
                                                        ).show()
                                                    }
                                                    90001 -> {
                                                        ToastUtils.makeText(
                                                            "稍后再看列表已满"
                                                        ).show()
                                                    }
                                                    90003 -> {
                                                        ToastUtils.makeText(
                                                            "视频不见了"
                                                        ).show()
                                                    }
                                                }
                                                onExpandBack()
                                            }

                                        }

                                    })
                                }
                                VideoOperationItem(
                                    iconResId = R.drawable.cloud_download,
                                    content = "缓存此视频"
                                ) {

                                }
                            }
                            Box(modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(parseColor("#242424"))
                                .padding(vertical = 6.dp)
                                .onGloballyPositioned {
                                    backButtonHeight = with(localDensity) { it.size.height.toDp() }
                                }) {
                                VideoOperationItem(
                                    icon = Icons.Outlined.ArrowBackIos,
                                    content = "返回继续浏览"
                                ) {
                                    onExpandBack()
                                }
                            }
                        }


                    } else {
                        Column(
                            modifier = (if (isExpand && videoBvid.isNotEmpty()) Modifier.height(
                                expandHeight
                            ) else Modifier).animateContentSize(animationSpec = tween(durationMillis = 400))
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
                                                .aspectRatio(
                                                    1.6f,
                                                    matchHeightConstraintsFirst = false
                                                )
                                                //.align(Alignment.CenterVertically)
                                                .clip(
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .offset(y = (1f).dp),
                                            contentScale = ContentScale.Crop,
                                        )
                                        if (badge.isNotEmpty()) {
                                            Text(
                                                text = badge,
                                                color = Color.White,
                                                fontSize = 7.sp,
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
                                        /*if (tagName.isNotEmpty()) {
                                            Text(
                                                text = tagName,
                                                color = Color.White,
                                                fontSize = 7.sp,
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
                                        }*/

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
                                        fontSize = 11.sp,
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
                                        fontSize = 8.sp,
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
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

    }


    @Composable
    fun VideoOperationItem(
        icon: ImageVector,
        content: String,
        onClick: () -> Unit = {},
    ) {
        val localDensity = LocalDensity.current
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickVfx { onClick() }, verticalAlignment = Alignment.CenterVertically
        ) {
            var textHeight by remember {
                mutableStateOf(0.dp)
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(textHeight),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = content,
                fontSize = 12.sp,
                fontFamily = puhuiFamily,
                color = Color.White,
                modifier = Modifier
                    .onGloballyPositioned {
                        textHeight =
                            with(localDensity) { it.size.height.toDp() }
                    }
            )
        }
    }

    @Composable
    fun VideoOperationItem(
        @DrawableRes iconResId: Int,
        content: String,
        onClick: () -> Unit = {},
    ) {
        val localDensity = LocalDensity.current
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .clickVfx { onClick() }, verticalAlignment = Alignment.CenterVertically
        ) {
            var textHeight by remember {
                mutableStateOf(0.dp)
            }
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(textHeight),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = content,
                fontSize = 12.sp,
                fontFamily = puhuiFamily,
                color = Color.White,
                modifier = Modifier
                    .onGloballyPositioned {
                        textHeight =
                            with(localDensity) { it.size.height.toDp() }
                    }
            )
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
                .clip(RoundedCornerShape(8.dp))
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
                    shape = RoundedCornerShape(8.dp)
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
                        .clip(RoundedCornerShape(6.dp)), contentDescription = null
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = areaInfo,
                        fontFamily = puhuiFamily,
                        color = Color.Gray, fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontFamily = puhuiFamily,
                        color = Color.Gray, fontSize = 10.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }   //番剧信息
            }
        }
    }
}