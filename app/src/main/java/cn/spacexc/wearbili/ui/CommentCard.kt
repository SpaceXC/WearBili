package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.comment.CommentRepliesActivity
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.parseColor
import coil.compose.AsyncImage
import coil.request.ImageRequest

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Composable
fun CommentCard(
    senderName: String,
    senderNameColor: String,
    senderAvatar: String,
    senderPendant: String,
    senderOfficialVerify: Int,
    senderMid: Long,
    sendTimeStamp: Long,
    commentContent: String,
    commentLikeCount: Int,
    commentRepliesCount: Int,
    commentReplies: Array<CommentContentData.Replies>,
    commentReplyControl: String,
    commentRpid: Long,
    isUpLiked: Boolean,
    isTopComment: Boolean,
    uploaderMid: Long,
    context: Context,
    isClickable: Boolean,
    videoAid: Long
) {
    Column(
        modifier = Modifier
            .clickVfx(isEnabled = isClickable) {
                if (isClickable) {
                    Intent(context, CommentRepliesActivity::class.java).apply {
                        putExtra("aid", videoAid)
                        putExtra("rootCommentId", commentRpid)
                        putExtra("upMid", uploaderMid)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }
            }
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 4.dp)
    ) {
        val localDensity = LocalDensity.current
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickVfx {
                Intent(context, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", senderMid)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
        ) {
            var pendentHeight by remember {
                mutableStateOf(0.dp)
            }
            var avatarBoxSize by remember {
                mutableStateOf(0.dp)
            }
            val avatarSizePx = with(localDensity) {
                avatarBoxSize.plus(4.dp).toPx()
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
            ) {
                if (senderPendant.isEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .size(avatarSizePx.times(0.9f).toInt())
                            .data(senderAvatar)
                            .placeholder(R.drawable.akari).crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(
                                avatarBoxSize
                                    .plus(4.dp)
                                    .times(0.9f)
                            )
                            //.fillMaxWidth()
                            //.padding(12.dp)
                            .clip(CircleShape)
                        //.aspectRatio(1f)
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(senderAvatar)
                            .size(avatarSizePx.times(0.7f).toInt())
                            .crossfade(true).placeholder(R.drawable.akari)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            //.fillMaxWidth()
                            .size(
                                pendentHeight.times(0.7f)
                            )
                            .clip(CircleShape)
                            .aspectRatio(1f)
                    )
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                        .size(avatarSizePx.times(1.4f).toInt())
                        .data(senderPendant)
                        .crossfade(true)
                        .build(),
                        contentDescription = null,
                        modifier = Modifier
                            //.fillMaxWidth()
                            .size(
                                avatarBoxSize
                                    .plus(4.dp)
                                    .times(1.4f)
                            )
                            .onGloballyPositioned {
                                pendentHeight = with(localDensity) {
                                    it.size.height.toDp()
                                }
                            }
                            .aspectRatio(1f)
                    )

                }
                if (senderOfficialVerify == OFFICIAL_TYPE_ORG || senderOfficialVerify == OFFICIAL_TYPE_PERSONAL) {
                    Image(
                        painter = painterResource(
                            id = when (senderOfficialVerify) {
                                OFFICIAL_TYPE_ORG -> R.drawable.flash_blue
                                OFFICIAL_TYPE_PERSONAL -> R.drawable.flash_yellow
                                else -> 0
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(avatarBoxSize.times(0.33f))
                            .align(Alignment.BottomEnd)
                            .offset(
                                x = if (senderPendant.isEmpty()) 0.dp else avatarBoxSize.times(-0.23f),
                                y = if (senderPendant.isEmpty()) 0.dp else avatarBoxSize.times(-0.23f)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.onGloballyPositioned {
                avatarBoxSize = with(localDensity) { it.size.height.toDp() }
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (senderMid == uploaderMid) {
                        Text(
                            text = "UP",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontFamily = googleSansFamily,
                            modifier = Modifier
                                .offset(y = (0.4).dp)
                                .clip(
                                    RoundedCornerShape(100)
                                )
                                .background(BilibiliPink)
                                .padding(
                                    start = 5.dp,
                                    end = 5.dp,
                                    top = 2.dp,
                                    bottom = 1.dp
                                )
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Text(
                        text = buildAnnotatedString {
                            /*if (isSenderUploader) {
                                withStyle(
                                    style = SpanStyle(
                                        color = BilibiliPink,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("[UP] ")
                                }
                            }*/
                            append(senderName)
                        },
                        fontFamily = puhuiFamily,
                        fontSize = 12.sp,
                        color = parseColor(senderNameColor),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sendTimeStamp.toDateStr("yyyy-MM-dd"),
                    fontFamily = googleSansFamily,
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(0.6f)
                )
            }

        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = buildAnnotatedString {
            if (isTopComment) {
                withStyle(style = SpanStyle(color = BilibiliPink, fontWeight = FontWeight.Bold)) {
                    append("[置顶] ")
                }
            }
            append(commentContent)
        }, color = Color.White, fontSize = 13.sp, fontFamily = puhuiFamily)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            CommentInfoItem(
                icon = Icons.Outlined.ThumbUp,
                content = commentLikeCount.toShortChinese()
            )
            Spacer(modifier = Modifier.width(4.dp))
            CommentInfoItem(icon = Icons.Outlined.ThumbDown, content = "点踩")
            Spacer(modifier = Modifier.weight(1f))
            CommentInfoItem(
                icon = painterResource(id = R.drawable.reply),
                content = "回复(${commentRepliesCount.toShortChinese()})"
            )
        }
        if (isUpLiked) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(100)
                    )
                    .background(BilibiliPink)
                    .padding(
                        start = 10.dp,
                        end = 9.5.dp,
                        top = 3.dp,
                        bottom = 4.dp
                    ), verticalAlignment = Alignment.CenterVertically
            ) {
                var textHeight by remember {
                    mutableStateOf(0.dp)
                }
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(textHeight),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "UP主觉得很赞",
                    fontSize = 10.sp,
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    modifier = Modifier
                        .onGloballyPositioned {
                            textHeight =
                                with(localDensity) { it.size.height.toDp() }
                        },
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (commentReplies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        parseColor("#262626")
                    )
                    .padding(8.dp)
            ) {
                commentReplies.forEach {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = if (it.member.mid == uploaderMid) BilibiliPink else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(it.member.uname)
                            }
                            append(": ")
                            append(it.content?.message ?: "")
                        },
                        fontSize = 11.sp,
                        color = Color(255, 255, 255, 179),
                        fontFamily = puhuiFamily,
                        maxLines = 2
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var textHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Text(
                        text = commentReplyControl,
                        fontSize = 11.sp,
                        fontFamily = puhuiFamily,
                        color = BilibiliPink,
                        modifier = Modifier
                            .onGloballyPositioned {
                                textHeight =
                                    with(localDensity) { it.size.height.toDp() }
                            },
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier
                            .size(textHeight - 4.dp)
                            .offset(y = (0.5).dp),
                        tint = BilibiliPink
                    )
                }
            }
        }
    }
}

@Composable
fun CommentInfoItem(
    icon: ImageVector,
    content: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val localDensity = LocalDensity.current
    Row(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
    }, verticalAlignment = Alignment.CenterVertically) {
        var textHeight by remember {
            mutableStateOf(0.dp)
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .alpha(0.6f)
                .size(textHeight),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = content,
            fontSize = 11.sp,
            fontFamily = puhuiFamily,
            color = Color.White,
            modifier = Modifier
                .alpha(0.6f)
                .onGloballyPositioned {
                    textHeight =
                        with(localDensity) { it.size.height.toDp() }
                }
        )
    }
}

@Composable
fun CommentInfoItem(
    icon: Painter,
    content: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val localDensity = LocalDensity.current
    Row(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
    }, verticalAlignment = Alignment.CenterVertically) {
        var textHeight by remember {
            mutableStateOf(0.dp)
        }
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .alpha(0.6f)
                .size(textHeight),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = content,
            fontSize = 11.sp,
            fontFamily = puhuiFamily,
            color = Color.White,
            modifier = Modifier
                .alpha(0.6f)
                .onGloballyPositioned {
                    textHeight =
                        with(localDensity) { it.size.height.toDp() }
                }
        )
    }
}