package cn.spacexc.wearbili.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.comment.COMMENT_TYPE
import cn.spacexc.wearbili.activity.comment.COMMENT_TYPE_VIDEO
import cn.spacexc.wearbili.activity.comment.PostActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.ui.CommentCard
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.viewmodel.CommentViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CommentFragment(private val isBangumiComment: Boolean = false) : Fragment() {
    val viewModel by viewModels<CommentViewModel>()

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                when (activityResult.data?.getIntExtra("code", 0)) {
                    0 -> {
                        val comment = Gson().fromJson(
                            activityResult.data?.getStringExtra("commentDataStr"),
                            CommentContentData::class.java
                        )
                        viewModel.appendFrontComment(comment)
                        lifecycleScope.launch {
                            viewModel.scrollState.animateScrollToItem(0)
                        }
                        ToastUtils.showText("发送成功")
                    }
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext())
    }

    fun refresh() {
        val aid = if (isBangumiComment) {
            (activity as BangumiActivity).viewModel.currentAid.value
        } else {
            VideoUtils.bv2av((activity as VideoActivity).videoId).toLong()
        }
        if (aid != 0L && aid != null) {
            viewModel.getComment(aid, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val aid = if (isBangumiComment) {
            (activity as BangumiActivity).viewModel.currentAid.value
        } else {
            VideoUtils.bv2av((activity as VideoActivity).videoId).toLong()
        }
        if (aid != 0L && aid != null) {
            viewModel.getComment(aid, true)
        }
        val upMid = if (isBangumiComment) {
            (activity as BangumiActivity).viewModel.bangumi.value?.result?.up_info?.mid
        } else {
            (activity as VideoActivity).currentVideo?.owner?.mid
        }
        if (isBangumiComment) {
            (activity as BangumiActivity).viewModel.currentAid.observe(viewLifecycleOwner) {
                if (it != null && it != 0L) {
                    viewModel.getComment(it, true)
                }
            }
        }
        (view as ComposeView).setContent {
            val aidState by if (isBangumiComment) (activity as BangumiActivity).viewModel.currentAid.observeAsState() else remember {
                mutableStateOf(aid)
            }
            val localDensity = LocalDensity.current
            val commentList by viewModel.commentList.observeAsState()
            val topComment by viewModel.topComment.observeAsState()
            val isError by viewModel.isError.observeAsState()
            Crossfade(targetState = aidState == 0L || aidState == null) { notSelected ->
                if (notSelected) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.Center)
                                .clickVfx {
                                    if (isBangumiComment) {
                                        (activity as BangumiActivity).pagerScrollTo(1)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.empty),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "还没有选择分集哦",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Crossfade(targetState = commentList == null) { isLoading ->
                        if (!isLoading) {
                            Crossfade(targetState = commentList.isNullOrEmpty()) { isEmpty ->
                                if (!isEmpty) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(
                                                width = (0.1).dp,
                                                color = Color(112, 112, 112, 112),
                                                shape = RoundedCornerShape(
                                                    topStart = 8.dp,
                                                    topEnd = 8.dp
                                                )
                                            )
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 8.dp,
                                                    topEnd = 8.dp
                                                )
                                            )
                                            .background(Color(36, 36, 36, 199)),
                                        contentPadding = PaddingValues(8.dp),
                                        state = viewModel.scrollState
                                    ) {
                                        item {
                                            var textHeight by remember {
                                                mutableStateOf(0.dp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickVfx {
                                                        activityResultLauncher.launch(
                                                            Intent(
                                                                requireActivity(),
                                                                PostActivity::class.java
                                                            ).apply {
                                                                putExtra(
                                                                    COMMENT_TYPE,
                                                                    COMMENT_TYPE_VIDEO
                                                                )
                                                                putExtra(
                                                                    "oid",
                                                                    (requireActivity() as VideoActivity).currentVideo?.aid
                                                                )
                                                            })
                                                    }
                                                    .border(
                                                        width = (0.1).dp,
                                                        color = Color(112, 112, 112, 112),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(36, 36, 36, 120))
                                                    .padding(14.dp)

                                            ) {
                                                Row(modifier = Modifier.align(Alignment.Center)) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Edit,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(textHeight)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "写一条友善的评论"/*(。・ω・。)*/,
                                                        color = Color.White,
                                                        fontFamily = puhuiFamily,
                                                        fontSize = 10.sp,
                                                        modifier = Modifier
                                                            /*.align(
                                                                Alignment.Center
                                                            )*/
                                                            .onGloballyPositioned {
                                                                textHeight =
                                                                    with(localDensity) { it.size.height.toDp() }
                                                            }
                                                    )
                                                }
                                            }
                                        }   //点我发评论捏
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
                                                    sendTimeStamp = comment.ctime.times(1000),
                                                    commentContent = comment.content?.message ?: "",
                                                    commentLikeCount = comment.like,
                                                    commentRepliesCount = comment.rcount,
                                                    commentReplies = comment.replies ?: emptyList(),
                                                    commentEmoteMap = comment.content?.emote
                                                        ?: emptyMap(),
                                                    commentJumpUrlMap = comment.content?.jump_url
                                                        ?: emptyMap(),
                                                    commentAttentionedUsersMap = comment.content?.at_name_to_mid
                                                        ?: emptyMap(),
                                                    commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                                        ?: "",
                                                    commentRpid = comment.rpid,
                                                    uploaderMid = upMid ?: 0,
                                                    isTopComment = true,
                                                    isUpLiked = comment.up_action.like,
                                                    context = requireContext(),
                                                    isClickable = true,
                                                    oid = aid ?: 0
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Divider(
                                                    modifier = Modifier.padding(horizontal = 2.dp),
                                                    color = Color(61, 61, 61, 255)
                                                )
                                            }
                                        }
                                        commentList?.forEach { comment ->
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
                                                    commentReplies = comment.replies ?: emptyList(),
                                                    commentEmoteMap = comment.content?.emote
                                                        ?: emptyMap(),
                                                    commentJumpUrlMap = comment.content?.jump_url
                                                        ?: emptyMap(),
                                                    commentAttentionedUsersMap = comment.content?.at_name_to_mid
                                                        ?: emptyMap(),
                                                    commentReplyControl = comment.reply_control?.sub_reply_entry_text
                                                        ?: "",
                                                    commentRpid = comment.rpid,
                                                    uploaderMid = upMid ?: 0L,
                                                    isTopComment = false,
                                                    isUpLiked = comment.up_action.like,
                                                    context = requireContext(),
                                                    isClickable = true,
                                                    oid = aid ?: 0
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
                                                if (aid != null && aid != 0L) {
                                                    viewModel.getComment(aid.toLong(), false)
                                                }
                                            })
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.Center)
                                                .clickVfx {
                                                    activityResultLauncher.launch(
                                                        Intent(
                                                            requireActivity(),
                                                            PostActivity::class.java
                                                        ).apply {
                                                            putExtra(
                                                                COMMENT_TYPE,
                                                                COMMENT_TYPE_VIDEO
                                                            )
                                                            putExtra(
                                                                "oid",
                                                                aid
                                                            )
                                                        }
                                                    )
                                                },
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.empty),
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "点我来发出第一条评论叭",
                                                color = Color.White,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Crossfade(targetState = isError) { error ->
                                if (error == true) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.Center)
                                                .clickVfx {
                                                    viewModel.isError.value = false
                                                    refresh()
                                                },
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.loading_2233_error),
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "加载失败, 点击重试",
                                                color = Color.White,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.Center),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.loading_2233),
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "玩命加载中",
                                                color = Color.White,
                                                fontFamily = puhuiFamily,
                                                fontWeight = FontWeight.Medium
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
    }
}