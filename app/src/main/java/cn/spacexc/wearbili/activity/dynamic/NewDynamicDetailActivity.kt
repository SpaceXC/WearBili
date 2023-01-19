package cn.spacexc.wearbili.activity.dynamic

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
import cn.spacexc.wearbili.ui.DynamicContent
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.viewmodel.DynamicViewModelNew

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/10.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class NewDynamicDetailActivity : AppCompatActivity() {
    val viewModel by viewModels<DynamicViewModelNew>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra("dyId")
        val oid = intent.getStringExtra("oid")
        val type = intent.getStringExtra("dyType")
        val mid = intent.getLongExtra("upMid", 0L)
        if (id != null && type != null && oid != null) {
            viewModel.getDynamicDetail(id)
            viewModel.getDynamicComments(true, oid, type)
        }
        setContent {
            val item by viewModel.dynamicDetailItem.observeAsState()
            val topComment by viewModel.topComment.observeAsState()
            val commentItems by viewModel.commentList.observeAsState()
            val commentCount by viewModel.commentCount.observeAsState()
            val isError by viewModel.isError.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "动态详情",
                onBack = ::finish,
                isLoading = item == null || (commentItems.isNullOrEmpty() && topComment == null),
                isError = isError == true,
                errorRetry = {
                    viewModel.isError.value = false
                    if (id != null && type != null && oid != null) {
                        viewModel.getDynamicDetail(id)
                        viewModel.getDynamicComments(true, oid, type)
                    }
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
                    item {
                        item?.let {
                            DynamicContent(
                                item = it,
                                context = this@NewDynamicDetailActivity
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "相关回复($commentCount)",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    topComment?.let { comment ->
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
                                senderIpLocation = comment.reply_control?.location ?: "",
                                sendTimeStamp = comment.ctime.times(1000),
                                commentContent = comment.content?.message ?: "",
                                commentLikeCount = comment.like,
                                commentRepliesCount = comment.rcount,
                                commentReplies = comment.replies ?: emptyList(),
                                commentEmoteMap = comment.content?.emote ?: emptyMap(),
                                commentJumpUrlMap = comment.content?.jump_url ?: emptyMap(),
                                commentAttentionedUsersMap = comment.content?.at_name_to_mid
                                    ?: emptyMap(),
                                commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                    ?: "",
                                commentRpid = comment.rpid,
                                uploaderMid = mid,
                                isTopComment = true,
                                isUpLiked = comment.up_action.like,
                                context = this@NewDynamicDetailActivity,
                                isClickable = true,
                                oid = oid?.toLong() ?: 0L
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                color = Color(61, 61, 61, 255)
                            )
                        }
                    }
                    commentItems?.forEach { comment ->
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
                                senderIpLocation = comment.reply_control?.location ?: "",
                                sendTimeStamp = comment.ctime.times(1000),
                                commentContent = comment.content?.message ?: "",
                                commentLikeCount = comment.like,
                                commentRepliesCount = comment.rcount,
                                commentReplies = comment.replies ?: emptyList(),
                                commentEmoteMap = comment.content?.emote ?: emptyMap(),
                                commentJumpUrlMap = comment.content?.jump_url ?: emptyMap(),
                                commentAttentionedUsersMap = comment.content?.at_name_to_mid
                                    ?: emptyMap(),
                                commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                    ?: "",
                                commentRpid = comment.rpid,
                                uploaderMid = mid,
                                isTopComment = false,
                                isUpLiked = comment.up_action.like,
                                context = this@NewDynamicDetailActivity,
                                isClickable = true,
                                oid = oid?.toLong() ?: 0L
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
                            if (id != null && type != null && oid != null) {
                                viewModel.getDynamicComments(false, oid, type)
                            }
                        })
                    }
                }
            }
        }
    }
}