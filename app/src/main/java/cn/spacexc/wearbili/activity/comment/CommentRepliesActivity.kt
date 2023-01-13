package cn.spacexc.wearbili.activity.comment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.CommentCard
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.viewmodel.CommentRepliesViewModel

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CommentRepliesActivity : AppCompatActivity() {
    val viewModel by viewModels<CommentRepliesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aid = intent.getLongExtra("aid", 0)
        val rootCommentId = intent.getLongExtra("rootCommentId", 0)
        val upMid = intent.getLongExtra("upMid", 0)
        viewModel.getCommentReplies(aid = aid, rootCommentId = rootCommentId, true)
        setContent {
            val rootComment by viewModel.rootComment.observeAsState()
            val commentReplies by viewModel.commentReplies.observeAsState()
            val repliesCount by viewModel.commentRepliesCount.observeAsState()
            val isError by viewModel.isError.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "评论详情",
                onBack = ::finish,
                isLoading = rootComment == null || commentReplies == null,
                isError = isError == true,
                errorRetry = {
                    viewModel.isError.value = false
                    viewModel.getCommentReplies(aid = aid, rootCommentId = rootCommentId, true)
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = (0.1).dp,
                            color = Color(112, 112, 112, 112),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color(36, 36, 36, 199)),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    rootComment?.let { comment ->
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            CommentCard(
                                senderName = comment.member?.uname ?: "",
                                senderAvatar = comment.member?.avatar ?: "",
                                senderNameColor = comment.member?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" },
                                senderPendant = comment.member?.pendant?.image_enhance
                                    ?: "",
                                senderOfficialVerify = comment.member?.official_verify?.type
                                    ?: -1,
                                senderMid = comment.member?.mid ?: 0,
                                sendTimeStamp = comment.ctime.times(1000),
                                commentContent = comment.content?.message ?: "",
                                commentLikeCount = comment.like,
                                commentRepliesCount = comment.rcount,
                                commentReplies = comment.replies ?: emptyArray(),
                                commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                    ?: "",
                                commentRpid = comment.rpid,
                                uploaderMid = upMid,
                                isTopComment = false,
                                isUpLiked = comment.up_action.like,
                                context = this@CommentRepliesActivity,
                                isClickable = false,
                                videoAid = aid
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    if (repliesCount != 0) {
                        item {
                            Text(
                                text = "相关回复($repliesCount)",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        commentReplies?.forEach { comment ->
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                CommentCard(
                                    senderName = comment.member?.uname ?: "",
                                    senderAvatar = comment.member?.avatar ?: "",
                                    senderNameColor = comment.member?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" },
                                    senderPendant = comment.member?.pendant?.image_enhance
                                        ?: "",
                                    senderOfficialVerify = comment.member?.official_verify?.type
                                        ?: -1,
                                    senderMid = comment.member?.mid ?: 0,
                                    sendTimeStamp = comment.ctime.times(1000),
                                    commentContent = comment.content?.message ?: "",
                                    commentLikeCount = comment.like,
                                    commentRepliesCount = comment.rcount,
                                    commentReplies = comment.replies ?: emptyArray(),
                                    commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                        ?: "",
                                    commentRpid = comment.rpid,
                                    uploaderMid = upMid,
                                    isTopComment = false,
                                    isUpLiked = comment.up_action.like,
                                    context = this@CommentRepliesActivity,
                                    isClickable = false,
                                    videoAid = aid
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(
                                    modifier = Modifier.padding(horizontal = 2.dp),
                                    color = Color(61, 61, 61, 255)
                                )
                            }
                        }
                        item {
                            LaunchedEffect(key1 = Unit, block = {
                                viewModel.getCommentReplies(
                                    aid = aid,
                                    rootCommentId = rootCommentId,
                                    false
                                )
                            })
                        }
                    }
                }
            }
        }
    }
}