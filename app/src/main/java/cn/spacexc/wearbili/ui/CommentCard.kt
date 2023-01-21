package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.comment.CommentRepliesActivity
import cn.spacexc.wearbili.activity.other.LinkProcessActivity
import cn.spacexc.wearbili.activity.search.SearchResultActivityNew
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.activity.video.VIDEO_ID_BV
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.EmoteObject
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.VideoUtils
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
fun RichText(
    isCommentReply: Boolean = false,
    replyUserName: AnnotatedString = buildAnnotatedString { },
    replyUserMid: Long = 0L,
    isTopComment: Boolean,
    origText: String,
    emoteMap: Map<String, EmoteObject>,
    jumpUrlMap: Map<String, CommentContentData.JumpUrlObject>,
    attentionUserMap: Map<String, Long>,
    fontSize: TextUnit,
    context: Context,
    onGloballyClicked: () -> Unit
) {
    val inlineTextContent = hashMapOf<String, InlineTextContent>()
    var temp = origText
    jumpUrlMap.forEach {
        temp = temp.replace(it.key, "///${it.key}///")
    }
    attentionUserMap.forEach {
        temp = temp.replace("@${it.key}", "///${it.key}///")
    }
    val list = temp.replace("[", "///").replace("]", "///").split("///")
    val annotatedString = buildAnnotatedString {
        if (isTopComment) {
            withStyle(style = SpanStyle(color = BilibiliPink, fontWeight = FontWeight.Bold)) {
                append("[置顶] ")
            }
        }
        if (isCommentReply) {
            if (replyUserMid != 0L) {
                pushStringAnnotation(
                    tag = "tagUser",
                    annotation = replyUserMid.toString()
                )
            }
            append(replyUserName)
            if (replyUserMid != 0L) {
                pop()
            }
        }
        list.forEach {
            if (emoteMap.containsKey("[$it]")) {
                appendInlineContent(id = "[$it]")
                inlineTextContent["[$it]"] = InlineTextContent(
                    placeholder = Placeholder(
                        width = fontSize.times(1.4f),
                        height = fontSize.times(1.4f),
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) { _ ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(emoteMap["[$it]"]?.url)/*.size(
                                with(localDensity) { fontSize.roundToPx() }
                            )*/.crossfade(true).build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (jumpUrlMap.containsKey(it)) {
                if (jumpUrlMap[it]?.extra?.is_word_search == true) {
                    pushStringAnnotation(
                        tag = "tagSearch",
                        annotation = jumpUrlMap[it]?.title ?: ""
                    )
                } else {
                    pushStringAnnotation(tag = "tagUrl", annotation = it)
                }
                if (jumpUrlMap[it]?.extra?.is_word_search == true) {
                    withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                        append(jumpUrlMap[it]?.title ?: "")
                    }
                }
                if (!jumpUrlMap[it]?.prefix_icon.isNullOrEmpty()) {
                    appendInlineContent(id = jumpUrlMap[it]?.prefix_icon ?: "")
                    inlineTextContent[jumpUrlMap[it]?.prefix_icon ?: ""] = InlineTextContent(
                        placeholder = Placeholder(
                            width = fontSize.times(1.3f),
                            height = fontSize.times(1.3f),
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) { _ ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(jumpUrlMap[it]?.prefix_icon)/*.size(
                                with(localDensity) { fontSize.roundToPx() }
                            )*/.crossfade(true).build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                if (jumpUrlMap[it]?.extra?.is_word_search != true) {
                    withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                        append(jumpUrlMap[it]?.title ?: "")
                    }
                }
                pop()
            } else if (attentionUserMap.containsKey(it)) {
                pushStringAnnotation(
                    tag = "tagUser",
                    annotation = attentionUserMap[it]?.toString() ?: ""
                )
                withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                    append("@$it")
                }
                pop()
            } else {
                withStyle(
                    style = if (isCommentReply) SpanStyle(
                        color = Color(
                            255,
                            255,
                            255,
                            179
                        )
                    ) else SpanStyle()
                ) {
                    append(it)
                }
            }
        }
    }
    ClickableText(
        text = annotatedString,
        onClick = { index ->
            annotatedString.getStringAnnotations(tag = "tagUser", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    Log.d(TAG, "RichText: tagUser: ${annotation.item}")
                    context.startActivity(Intent(context, SpaceProfileActivity::class.java).apply {
                        putExtra("userMid", annotation.item.toLong())
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    return@ClickableText
                }
            annotatedString.getStringAnnotations(tag = "tagUrl", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    if (VideoUtils.isBV(annotation.item)) {
                        Intent(context, VideoActivity::class.java).apply {
                            putExtra("videoId", annotation.item)
                            putExtra("videoIdType", VIDEO_ID_BV)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (VideoUtils.isAV(annotation.item)) {
                        Intent(context, VideoActivity::class.java).apply {
                            putExtra("videoId", VideoUtils.av2bv(annotation.item))
                            putExtra("videoIdType", VIDEO_ID_BV)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (annotation.item.startsWith("http")) {
                        context.startActivity(
                            Intent(
                                context,
                                LinkProcessActivity::class.java
                            ).apply {
                                putExtra("url", annotation.item)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                    }
                    return@ClickableText
                }
            annotatedString.getStringAnnotations(tag = "tagSearch", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(context, SearchResultActivityNew::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("keyword", annotation.item.log("keyword"))
                    context.startActivity(intent)
                    return@ClickableText
                }
            Log.d(TAG, "RichText: Global Click Event")
            onGloballyClicked()
        }, style = TextStyle(
            fontFamily = puhuiFamily,
            color = Color.White,
            fontSize = fontSize
        ), inlineTextContent = inlineTextContent
    )
}

@Composable
fun CommentCard(
    senderName: String,
    senderNameColor: String,
    senderAvatar: String,
    senderPendant: String,
    senderOfficialVerify: Int,
    senderMid: Long,
    sendTimeStamp: Long,
    senderIpLocation: String,
    commentContent: String,
    commentLikeCount: Int,
    commentRepliesCount: Int,
    commentReplies: List<CommentContentData.Replies>,
    commentReplyControl: String,
    commentRpid: Long,
    commentEmoteMap: Map<String, EmoteObject>,
    commentAttentionedUsersMap: Map<String, Long>,
    commentJumpUrlMap: Map<String, CommentContentData.JumpUrlObject>,
    isUpLiked: Boolean,
    isTopComment: Boolean,
    uploaderMid: Long,
    context: Context,
    isClickable: Boolean,
    oid: Long,
) {
    Column(
        modifier = Modifier
            .clickVfx(isEnabled = isClickable) {
                if (isClickable) {
                    Intent(context, CommentRepliesActivity::class.java).apply {
                        putExtra("oid", oid)
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
                //modifier = Modifier.padding(0.dp)
            ) {
                if (senderPendant.isEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            //.size(avatarSizePx.times(0.9f).toInt())
                            .data(senderAvatar)
                            .placeholder(R.drawable.akari).crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(
                                avatarBoxSize
                                    //.plus(4.dp)
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
                            //.size(avatarSizePx.times(0.7f).toInt())
                            .crossfade(true).placeholder(R.drawable.akari)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            //.fillMaxWidth()
                            .size(
                                pendentHeight.times(0.6f)
                            )
                            .clip(CircleShape)
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
                                //.plus(8.dp)
                                //.times(1.wf)
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
                                OFFICIAL_TYPE_ORG -> R.drawable.flash_business
                                OFFICIAL_TYPE_PERSONAL -> R.drawable.flash_personal
                                else -> 0
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(avatarBoxSize.times(0.33f))
                            .align(Alignment.BottomEnd)
                            .offset(
                                x = if (senderPendant.isEmpty()) 0.dp else avatarBoxSize.times(
                                    -0.1f
                                ),
                                y = if (senderPendant.isEmpty()) 0.dp else avatarBoxSize.times(
                                    -0.1f
                                )
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.onGloballyPositioned {
                avatarBoxSize = with(localDensity) { it.size.height.toDp() }
            }
            ) {
                Text(
                    text = senderName,
                    fontFamily = puhuiFamily,
                    fontSize = 9.sp,
                    color = parseColor(senderNameColor),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = sendTimeStamp.toDateStr("yyyy-MM-dd"),
                    fontFamily = puhuiFamily,
                    fontSize = 8.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(0.6f),
                    maxLines = 1
                )
                Text(
                    text = senderIpLocation,
                    fontFamily = puhuiFamily,
                    fontSize = 8.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(0.6f),
                    maxLines = 1
                )
            }

        }
        Spacer(modifier = Modifier.height(4.dp))
        RichText(
            isTopComment = isTopComment,
            origText = commentContent,
            emoteMap = commentEmoteMap,
            jumpUrlMap = commentJumpUrlMap,
            attentionUserMap = commentAttentionedUsersMap,
            fontSize = 9.sp,
            context = context
        ) {
            if (isClickable) {
                Intent(context, CommentRepliesActivity::class.java).apply {
                    putExtra("oid", oid)
                    putExtra("rootCommentId", commentRpid)
                    putExtra("upMid", uploaderMid)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
        }
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
                    fontSize = 8.sp,
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
                    RichText(
                        isTopComment = false,
                        origText = it.content?.message ?: "",
                        emoteMap = it.content?.emote ?: emptyMap(),
                        jumpUrlMap = it.content?.jump_url ?: emptyMap(),
                        attentionUserMap = it.content?.at_name_to_mid ?: emptyMap(),
                        fontSize = 9.sp,
                        isCommentReply = true,
                        context = context,
                        replyUserName = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = if (it.member.mid == uploaderMid) BilibiliPink else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(it.member.uname)
                            }
                            append(": ")
                        },
                        replyUserMid = it.member.mid
                    ) {
                        if (isClickable) {
                            Intent(context, CommentRepliesActivity::class.java).apply {
                                putExtra("oid", oid)
                                putExtra("rootCommentId", commentRpid)
                                putExtra("upMid", uploaderMid)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(this)
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var textHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Text(
                        text = commentReplyControl,
                        fontSize = 9.sp,
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
            fontSize = 9.sp,
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
            fontSize = 9.sp,
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