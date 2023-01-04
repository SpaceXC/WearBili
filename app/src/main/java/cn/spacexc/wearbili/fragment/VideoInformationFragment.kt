package cn.spacexc.wearbili.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.image.PhotoViewActivity
import cn.spacexc.wearbili.activity.settings.ChooseSettingsActivity
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.activity.video.NewVideoCacheActivity
import cn.spacexc.wearbili.activity.video.PlayOnPhoneActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.ViewFullVideoPartsActivity
import cn.spacexc.wearbili.dataclass.RoundButtonDataNew
import cn.spacexc.wearbili.manager.ID_TYPE_SSID
import cn.spacexc.wearbili.manager.SettingsManager
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.utils.parseColor
import cn.spacexc.wearbili.viewmodel.VideoViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson

class VideoInformationFragment : Fragment() {
    val viewModel by viewModels<VideoViewModel>()
    lateinit var activity: VideoActivity

    private val btnListUpperRow = listOf(
        RoundButtonDataNew(Icons.Outlined.PlayCircle, "播放", "播放"),
        //RoundButtonDataNew(Icons.Outlined.ThumbUp, "点赞", "点赞"),
        //RoundButtonDataNew(Icons.Outlined.MonetizationOn, "投币", "投币"),
        //RoundButtonDataNew(Icons.Outlined.StarBorder, "收藏", "收藏"),
        //RoundButtonDataNew(Icons.Outlined.ThumbDown, "点踩", "点踩"),
        RoundButtonDataNew(Icons.Outlined.History, "稍后再看", "稍后再看"),
        RoundButtonDataNew(Icons.Outlined.SendToMobile, "手机观看", "手机观看"),
        RoundButtonDataNew(Icons.Outlined.CloudDownload, "缓存", "缓存")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return context?.let { ComposeView(it) }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = getActivity() as VideoActivity
        viewModel.getVideoInfo(activity.videoId)
        viewModel.getIsLiked(activity.videoId)
        viewModel.getIsCoined(activity.videoId)
        viewModel.getIsFavorite(activity.videoId)
        viewModel.getSubtitle(activity.videoId)
        viewModel.videoInfo.observe(
            viewLifecycleOwner
        ) {
            activity.currentVideo = it.data
            viewModel.getFans(it.data.owner.mid)
            viewModel.getUploaderInfo(it.data.owner.mid)
            VideoManager.uploadVideoViewingProgress(
                it.data.bvid,
                it.data.history?.cid ?: it.data.cid,
                it.data.history?.progress?.toInt() ?: 0
            )
            if (it.data.season?.season_id?.isNotEmpty() == true) {
                Intent(requireActivity(), BangumiActivity::class.java).apply {
                    putExtra("id", it.data.season.season_id)
                    putExtra("idType", ID_TYPE_SSID)
                    startActivity(this)
                    activity.finish()
                }
            }
        }
        (view as ComposeView).setContent {
            //var buttonItemHeight by remember { mutableStateOf(0.dp) }
            val localDensity = LocalDensity.current
            val videoInfo by viewModel.videoInfo.observeAsState()
            val userFans by viewModel.uploaderFans.observeAsState()
            val uploaderInfo by viewModel.uploaderInfo.observeAsState()
            val isLiked by viewModel.isLiked.observeAsState()
            val isFavorite by viewModel.isFavorite.observeAsState()
            val isCoined by viewModel.isCoined.observeAsState()
            var descriptionMaxLines by remember {
                mutableStateOf(3)
            }
            //val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = viewModel.scrollState)
                    .padding(vertical = 8.dp, horizontal = 10.dp)
                    .animateContentSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .placeholder(R.drawable.placeholder)
                        .data(videoInfo?.data?.pic).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween(durationMillis = 200))
                        .clickVfx {
                            val intent =
                                Intent(requireActivity(), PhotoViewActivity::class.java)
                            intent.putExtra("imageUrl", videoInfo?.data?.pic)
                            startActivity(intent)
                        }
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = videoInfo?.data?.title ?: "",
                    color = Color.White,
                    fontFamily = puhuiFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clickVfx {
                            Intent(requireActivity(), SpaceProfileActivity::class.java).apply {
                                putExtra("userMid", uploaderInfo?.data?.mid)
                                startActivity(this)
                            }
                        }
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(Color(36, 36, 36, 120))
                        .border(
                            width = 0.01.dp,
                            color = Color(112, 112, 112, 112),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                        .fillMaxWidth()

                ) {
                    var infoHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(videoInfo?.data?.owner?.face).crossfade(true).build(),
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .size(infoHeight)
                            .animateContentSize(animationSpec = tween(durationMillis = 200)),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .onGloballyPositioned {
                                infoHeight = with(localDensity) {
                                    it.size.height.toDp()
                                }
                            }, verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = videoInfo?.data?.owner?.name ?: "",
                            color = parseColor(uploaderInfo?.data?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" }),
                            fontFamily = puhuiFamily,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${userFans?.data?.card?.fans?.toShortChinese() ?: "0"}粉丝",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }   //用户信息
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    VideoInfoItem(
                        icon = Icons.Outlined.PlayCircle,
                        content = "${videoInfo?.data?.stat?.view?.toShortChinese() ?: "0"}播放"
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    VideoInfoItem(
                        icon = painterResource(id = R.drawable.ic_danmaku),
                        content = "${videoInfo?.data?.stat?.danmaku ?: "0"}弹幕"
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    VideoInfoItem(
                        icon = Icons.Outlined.Movie,
                        content = videoInfo?.data?.bvid ?: "",
                        onLongClick = {
                            val clipboardManager: ClipboardManager =
                                ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                            val clip: ClipData =
                                ClipData.newPlainText("wearbili bvid", videoInfo?.data?.bvid ?: "")
                            clipboardManager.setPrimaryClip(clip)
                            ToastUtils.makeText("已复制BV号")
                                .show()
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    VideoInfoItem(
                        icon = Icons.Outlined.CalendarMonth,
                        content = videoInfo?.data?.pubdate?.times(1000)?.toDateStr() ?: ""
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (videoInfo?.data?.desc?.isNotEmpty() == true) {
                    Text(text = videoInfo?.data?.desc ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(animationSpec = tween(durationMillis = 400))
                            .clickable {
                                descriptionMaxLines =
                                    if (descriptionMaxLines == 3) Int.MAX_VALUE else 3
                            }
                            .alpha(0.9f),
                        fontSize = 13.sp,
                        color = Color.White,
                        fontFamily = puhuiFamily,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (videoInfo?.data?.pages?.size != 1) {
                    Text(
                        text = "选集(${videoInfo?.data?.pages?.size ?: 0})",
                        fontFamily = puhuiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.clickVfx {
                            videoInfo?.data?.pages?.let {
                                val intent = Intent(
                                    requireActivity(),
                                    ViewFullVideoPartsActivity::class.java
                                )
                                intent.putExtra(
                                    "data",
                                    Gson().toJson(videoInfo?.data?.pages?.let {
                                        cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(
                                            it
                                        )
                                    })
                                )
                                intent.putExtra("bvid", videoInfo?.data?.bvid)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                Application.getContext().startActivity(intent)
                            }

                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .requiredSizeIn(maxHeight = 140.dp)
                        ) {
                            videoInfo?.data?.pages?.forEachIndexed { index, page ->
                                item {
                                    Column(modifier = Modifier.clickVfx {
                                        SettingsManager.playVideo(
                                            context = requireContext(),
                                            bvid = videoInfo?.data?.bvid,
                                            cid = videoInfo?.data?.cid,
                                            title = videoInfo?.data?.title,
                                            progress = videoInfo?.data?.history?.progress ?: 0,
                                            subtitleUrl = null
                                        )
                                    }) {
                                        Column(
                                            modifier = Modifier
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
                                                text = "P${index + 1} ${page.part}",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontFamily = puhuiFamily,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.alpha(0.76f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }   //部分选集
                        if ((videoInfo?.data?.pages?.size ?: 0) > 3) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0, 0, 0, 204)
                                            )
                                        )
                                    )
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.BottomCenter),
                            )   //阴影
                        }
                    }   //部分选集
                }
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), modifier = Modifier
                        .fillMaxWidth()
                        .requiredSizeIn(maxHeight = 4000.dp)
                ) {
                    btnListUpperRow.forEach { buttonItem ->
                        item {
                            AnimatedContent(
                                targetState = when (buttonItem.buttonName) {
                                    "点赞" -> isLiked
                                    "投币" -> isCoined
                                    "收藏" -> isFavorite
                                    else -> false
                                }
                            ) {
                                RoundButton(
                                    buttonItem = buttonItem,
                                    //videoInfo = videoInfo,
                                    onLongClick = {
                                        when (buttonItem.buttonName) {
                                            "播放" -> {
                                                Log.d(
                                                    Application.TAG,
                                                    "setOnLongClickListener: "
                                                )
                                                val intent =
                                                    Intent(
                                                        context,
                                                        ChooseSettingsActivity::class.java
                                                    )
                                                val item =
                                                    SettingsManager.getSettingByName("defaultPlayer")
                                                intent.putExtra("item", item)
                                                /*intent.putExtra("itemKey", item?.settingName)
                                                intent.putExtra("itemName", item?.displayName)
                                                intent.putExtra("defVal", item?.defString)*/
                                                startActivity(intent)
                                            }
                                        }
                                    }, tint =
                                    when (buttonItem.buttonName) {
                                        "点赞" -> if (isLiked == true) BilibiliPink else Color.White
                                        "投币" -> if (isCoined == true) BilibiliPink else Color.White
                                        "收藏" -> if (isFavorite == true) BilibiliPink else Color.White
                                        else -> Color.White
                                    }
                                ) {
                                    when (buttonItem.buttonName) {
                                        "手机观看" -> {
                                            if (isAdded) {
                                                val intent = Intent(
                                                    requireActivity(),
                                                    PlayOnPhoneActivity::class.java
                                                )
                                                intent.putExtra(
                                                    "qrCodeUrl",
                                                    "https://www.bilibili.com/video/${activity.videoId}"
                                                )
                                                startActivity(intent)
                                            }
                                        }
                                        "点赞" -> {
                                            videoInfo?.let {
                                                viewModel.likeVideo(
                                                    it.data.bvid,
                                                    isLiked ?: true
                                                )
                                            }
                                        }
                                        "播放" -> {
                                            SettingsManager.playVideo(
                                                context = requireContext(),
                                                bvid = videoInfo?.data?.bvid,
                                                cid = videoInfo?.data?.cid,
                                                title = videoInfo?.data?.title,
                                                progress = videoInfo?.data?.history?.progress
                                                    ?: 0,
                                                subtitleUrl = if (viewModel.subtitle.value?.data?.subtitle?.list.isNullOrEmpty()) null else viewModel.subtitle.value?.data?.subtitle?.list?.get(
                                                    0
                                                )?.subtitleUrl
                                            )
                                        }
                                        "稍后再看" -> {
                                            videoInfo?.data?.bvid?.let { bvid ->
                                                viewModel.addToViewLater(bvid)
                                            }
                                        }
                                        "缓存" -> {
                                            Intent(
                                                requireActivity(),
                                                NewVideoCacheActivity::class.java
                                            ).apply {
                                                putExtra("bvid", videoInfo?.data?.bvid ?: "")
                                                putExtra("cid", videoInfo?.data?.cid ?: 0)
                                                putExtra("title", videoInfo?.data?.title)
                                                putExtra("coverUrl", videoInfo?.data?.pic)
                                                putExtra(
                                                    "data",
                                                    Gson().toJson(videoInfo?.data?.pages?.let {
                                                        cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(
                                                            it
                                                        )
                                                    })
                                                )
                                                startActivity(this)
                                            }
                                        }
                                    }
                                }
                            }


                            /*Column(modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(
                                    Unit
                                ) {
                                    detectTapGestures(onTap = {
                                        when (buttonItem.buttonName) {
                                            "手机观看" -> {
                                                if (isAdded) {
                                                    val intent = Intent(
                                                        requireActivity(),
                                                        PlayOnPhoneActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        "qrCodeUrl",
                                                        "https://www.bilibili.com/video/${activity.videoId}"
                                                    )
                                                    startActivity(intent)
                                                }
                                            }
                                            "点赞" -> {
                                                videoInfo?.let {
                                                    viewModel.likeVideo(
                                                        it.data.bvid,
                                                        isLiked ?: true
                                                    )
                                                }
                                            }
                                            "播放" -> {
                                                SettingsManager.playVideo(
                                                    context = requireContext(),
                                                    bvid = videoInfo?.data?.bvid,
                                                    cid = videoInfo?.data?.cid,
                                                    title = videoInfo?.data?.title,
                                                    progress = videoInfo?.data?.history?.progress
                                                        ?: 0
                                                )
                                            }
                                            "稍后再看" -> {
                                                videoInfo?.data?.bvid?.let { bvid ->
                                                    viewModel.addToViewLater(bvid)
                                                }
                                            }
                                        }
                                    }, onLongPress = {
                                        when (buttonItem.buttonName) {
                                            "播放" -> {
                                                Log.d(
                                                    Application.TAG,
                                                    "setOnLongClickListener: "
                                                )
                                                val intent =
                                                    Intent(
                                                        context,
                                                        ChooseSettingsActivity::class.java
                                                    )
                                                val item =
                                                    SettingsManager.getSettingByName("defaultPlayer")
                                                intent.putExtra("item", item)
                                                *//*intent.putExtra("itemKey", item?.settingName)
                                                intent.putExtra("itemName", item?.displayName)
                                                intent.putExtra("defVal", item?.defString)*//*
                                                startActivity(intent)
                                            }
                                        }

                                    })
                                }
                                .onGloballyPositioned {
                                    buttonItemHeight = with(localDensity) { it.size.height.toDp() }
                                }, verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .align(Alignment.Center)
                                            .clip(CircleShape)
                                            .border(
                                                width = 0.1.dp, color = Color(
                                                    91, 92, 93, 204
                                                ), shape = CircleShape
                                            )
                                            .background(Color(41, 41, 41, 204))
                                    ) {
                                        Icon(
                                            imageVector = buttonItem.icon,
                                            contentDescription = null,
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = when (buttonItem.buttonName) {
                                                "点赞" -> if (isLiked == true) BilibiliPink else Color.White
                                                "投币" -> if (isCoined == true) BilibiliPink else Color.White
                                                "收藏" -> if (isFavorite == true) BilibiliPink else Color.White
                                                else -> Color.White
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buttonItem.displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = puhuiFamily,
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    maxLines = 1
                                )
                            }*/
                        }
                    }

                }
            }
        }
    }

    @Composable
    fun RoundButton(
        buttonItem: RoundButtonDataNew,
        //videoInfo: VideoDetailInfo?,
        onLongClick: () -> Unit,
        tint: Color,
        onClick: () -> Unit
    ) {
        val localDensity = LocalDensity.current
        var buttonItemHeight by remember {
            mutableStateOf(0.dp)
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .pointerInput(
                Unit
            ) {
                detectTapGestures(onTap = {
                    onClick()
                }, onLongPress = {
                    onLongClick()

                })
            }
            .onGloballyPositioned {
                buttonItemHeight = with(localDensity) { it.size.height.toDp() }
            }, verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .border(
                            width = 0.1.dp, color = Color(
                                91, 92, 93, 204
                            ), shape = CircleShape
                        )
                        .background(Color(41, 41, 41, 204))
                ) {
                    Icon(
                        imageVector = buttonItem.icon,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = tint
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buttonItem.displayName,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontFamily = puhuiFamily,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                maxLines = 1
            )
        }
    }

    @Composable
    fun VideoInfoItem(
        icon: ImageVector,
        content: String,
        onClick: () -> Unit = {},
        onLongClick: () -> Unit = {},
    ) {
        val localDensity = LocalDensity.current
        Row(modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
        }) {
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
                fontSize = 12.sp,
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
    fun VideoInfoItem(
        icon: Painter,
        content: String,
        onClick: () -> Unit = {},
        onLongClick: () -> Unit = {},
    ) {
        val localDensity = LocalDensity.current
        Row(modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
        }) {
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
            Text(text = content,
                fontSize = 12.sp,
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
}