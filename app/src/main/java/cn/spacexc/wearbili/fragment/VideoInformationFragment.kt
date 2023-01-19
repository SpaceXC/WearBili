package cn.spacexc.wearbili.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import cn.spacexc.wearbili.activity.other.PARAM_QRCODE_MESSAGE
import cn.spacexc.wearbili.activity.other.PARAM_QRCODE_URL
import cn.spacexc.wearbili.activity.other.QrCodeActivityNew
import cn.spacexc.wearbili.activity.settings.ChooseSettingsActivity
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.activity.video.*
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.RoundButtonDataNew
import cn.spacexc.wearbili.manager.*
import cn.spacexc.wearbili.ui.*
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.TimeUtils.secondToTime
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return context?.let { ComposeView(it) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getIsLiked(activity.videoId)
        viewModel.getIsCoined(activity.videoId)
        viewModel.getIsFavorite(activity.videoId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = getActivity() as VideoActivity
        viewModel.getVideoInfo(activity.videoId)
        viewModel.getProgress(activity.videoId)
        viewModel.videoInfo.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                activity.currentVideo = it.data
                viewModel.getFans(it.data.owner.mid)
                viewModel.getUploaderInfo(it.data.owner.mid)
                VideoManager.uploadVideoViewingProgress(
                    it.data.bvid,
                    viewModel.historyCid ?: it.data.cid,
                    viewModel.historyCid?.toInt() ?: 0
                )
            }
        }
        viewModel.relatedSeasonId.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Intent(requireActivity(), BangumiActivity::class.java).apply {
                    putExtra("id", it)
                    putExtra("idType", ID_TYPE_SSID)
                    startActivity(this)
                    activity.finish()


                }
            }
        }
        viewModel.relatedEpid.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Intent(requireActivity(), BangumiActivity::class.java).apply {
                    putExtra("id", it)
                    putExtra("idType", ID_TYPE_EPID)
                    startActivity(this)
                    activity.finish()

                }
            }
        }
        (view as ComposeView).setContent {
            val localDensity = LocalDensity.current
            val videoInfo by viewModel.videoInfo.observeAsState()
            val userFans by viewModel.uploaderFans.observeAsState()
            val uploaderInfo by viewModel.uploaderInfo.observeAsState()
            val isLiked by viewModel.isLiked.observeAsState()
            val isFavorite by viewModel.isFavorite.observeAsState()
            val isCoined by viewModel.isCoined.observeAsState()
            val isError by viewModel.isError.observeAsState()
            var descriptionMaxLines by remember {
                mutableStateOf(3)
            }
            val likeColor by animateColorAsState(targetValue = if (isLiked == true) BilibiliPink else Color.White)
            val coinColor by animateColorAsState(targetValue = if (isCoined == true) BilibiliPink else Color.White)
            val favColor by animateColorAsState(targetValue = if (isFavorite == true) BilibiliPink else Color.White)

            Crossfade(targetState = videoInfo != null) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = viewModel.scrollState)
                            .padding(vertical = 8.dp)
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(animationSpec = tween(durationMillis = 200))
                                .clickVfx {
                                    val intent =
                                        Intent(
                                            requireActivity(),
                                            PhotoViewActivity::class.java
                                        )
                                    intent.putExtra("imageUrl", videoInfo?.data?.pic)
                                    startActivity(intent)
                                }
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .placeholder(R.drawable.placeholder)
                                        .data(videoInfo?.data?.pic).crossfade(true).build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(animationSpec = tween(durationMillis = 200)),
                                    contentScale = ContentScale.FillWidth
                                )
                                Text(
                                    text = videoInfo?.data?.duration?.secondToTime() ?: "",
                                    fontFamily = googleSansFamily,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(
                                            BottomEnd
                                        )
                                        .padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = videoInfo?.data?.title ?: "",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        if (!videoInfo?.data?.staff.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                var rowHeight by remember {
                                    mutableStateOf(0.dp)
                                }
                                val state = rememberScrollState()
                                val sixteenDpsToPx = with(localDensity) {
                                    16.dp.toPx()
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(state)
                                        .onGloballyPositioned {
                                            rowHeight = with(localDensity) { it.size.height.toDp() }
                                        }
                                ) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    videoInfo?.data?.staff?.forEach { staff ->
                                        var textHeight by remember {
                                            mutableStateOf(0.dp)
                                        }
                                        var avatarHeight by remember {
                                            mutableStateOf(0.dp)
                                        }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickVfx {
                                                    Intent(
                                                        context,
                                                        SpaceProfileActivity::class.java
                                                    ).apply {
                                                        putExtra("userMid", staff.mid)
                                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                        requireContext().startActivity(this)
                                                    }
                                                }
                                                .border(
                                                    width = (0.1).dp,
                                                    color = Color(112, 112, 112, 112),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(36, 36, 36, 166))
                                                .padding(12.dp)
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(staff.face).crossfade(true).build(),
                                                    contentDescription = null, modifier = Modifier
                                                        .size(textHeight.times(1.1f))
                                                        .clip(CircleShape)
                                                        .onGloballyPositioned {
                                                            avatarHeight =
                                                                with(localDensity) { it.size.height.toDp() }
                                                        }
                                                )
                                                if (staff.official.type == OFFICIAL_TYPE_ORG || staff.official.type == OFFICIAL_TYPE_PERSONAL) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = when (staff.official.type) {
                                                                OFFICIAL_TYPE_ORG -> R.drawable.flash_blue
                                                                OFFICIAL_TYPE_PERSONAL -> R.drawable.flash_yellow
                                                                else -> 0
                                                            }
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(avatarHeight.times(0.25f))
                                                            .align(Alignment.BottomEnd)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Column(modifier = Modifier.onGloballyPositioned {
                                                textHeight =
                                                    with(localDensity) { it.size.height.toDp() }
                                            }) {
                                                Text(
                                                    text = staff.name,
                                                    color = parseColor(staff.vip.label.bg_color.ifNullOrEmpty { "#FFFFFF" }),
                                                    fontFamily = puhuiFamily,
                                                    fontSize = 11.sp
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = staff.title,
                                                    color = Color.White,
                                                    fontFamily = puhuiFamily,
                                                    fontSize = 8.sp,
                                                    modifier = Modifier.alpha(0.7f)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .alpha(state.value / sixteenDpsToPx)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0, 0, 0, 204),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                            .height(rowHeight)
                                            .width(32.dp),
                                    )

                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .alpha((state.maxValue - state.value) / sixteenDpsToPx)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color(0, 0, 0, 204),

                                                        )
                                                )
                                            )
                                            .height(rowHeight)
                                            .width(32.dp),
                                    )
                                }
                            }
                        } else {
                            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                UserCard(
                                    name = videoInfo?.data?.owner?.name ?: "",
                                    uid = videoInfo?.data?.owner?.mid ?: 0,
                                    sign = "${userFans?.data?.card?.fans?.toShortChinese() ?: "0"}粉丝",
                                    avatar = videoInfo?.data?.owner?.face ?: "",
                                    pendant = uploaderInfo?.data?.pendant?.image_enhance ?: "",
                                    nicknameColor = uploaderInfo?.data?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" },
                                    officialType = uploaderInfo?.data?.official?.type
                                        ?: OFFICIAL_TYPE_NONE
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                VideoInfoItem(
                                    icon = Icons.Outlined.PlayCircle,
                                    content = "${videoInfo?.data?.stat?.view?.toShortChinese() ?: "0"}播放"
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                VideoInfoItem(
                                    icon = painterResource(id = R.drawable.ic_danmaku),
                                    content = "${videoInfo?.data?.stat?.danmaku?.toShortChinese() ?: "0"}弹幕"
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
                                            ClipData.newPlainText(
                                                "wearbili bvid",
                                                videoInfo?.data?.bvid ?: ""
                                            )
                                        clipboardManager.setPrimaryClip(clip)
                                        ToastUtils.makeText("已复制BV号")
                                            .show()
                                    }
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                VideoInfoItem(
                                    icon = Icons.Outlined.CalendarMonth,
                                    content = videoInfo?.data?.pubdate?.times(1000)?.toDateStr()
                                        ?: ""
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            videoInfo?.data?.honor_reply?.honor?.forEach { honor ->
                                /*Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            parseColor(honor.bgColorNight)
                                        )
                                        .padding(8.dp)
                                ) {
                                    var textHeight by remember {
                                        mutableStateOf(0.dp)
                                    }
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(honor.iconNight)
                                            .crossfade(true)*//*.size(with(localDensity){textHeight.roundToPx()})*//*
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier.size(textHeight)
                                    )
                                    Text(
                                        text = honor.text,
                                        fontSize = 10.sp,
                                        color = parseColor(honor.textColorNight),
                                        fontFamily = puhuiFamily,
                                        modifier = Modifier.onGloballyPositioned {
                                            textHeight =
                                                with(localDensity) { it.size.height.toDp() }
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))*/
                            }

                            if (videoInfo?.data?.desc?.isNotEmpty() == true) {
                                Text(
                                    text = videoInfo?.data?.desc ?: "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(animationSpec = tween(durationMillis = 400))
                                        .clickable {
                                            descriptionMaxLines =
                                                if (descriptionMaxLines == 3) Int.MAX_VALUE else 3
                                        }
                                        .alpha(0.9f),
                                    fontSize = 10.sp,
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
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.clickVfx {
                                        videoInfo?.data?.pages?.let { pages ->
                                            val intent = Intent(
                                                requireActivity(),
                                                ViewFullVideoPartsActivity::class.java
                                            )
                                            intent.putExtra(
                                                "data",
                                                Gson().toJson(
                                                    cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(
                                                        pages
                                                    )
                                                )
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
                                                        cid = page.cid,
                                                        title = "P${index + 1} ${page.part} - ${videoInfo?.data?.title}",
                                                        //progress = videoInfo?.data?.history?.progress ?: 0,
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

                                                            .background(
                                                                color = Color(
                                                                    36,
                                                                    36,
                                                                    36,
                                                                    199
                                                                )
                                                            )
                                                            .padding(
                                                                vertical = 12.dp,
                                                                horizontal = 16.dp
                                                            )
                                                            .fillMaxWidth(),
                                                    ) {
                                                        Text(
                                                            text = "P${index + 1} ${page.part}",
                                                            color = Color.White,
                                                            fontSize = 10.sp,
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

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.PlayCircle,
                                            "播放",
                                            "播放"
                                        ), onLongClick = {
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
                                        }, tint = Color.White, modifier = Modifier.weight(1f)
                                    ) {
                                        SettingsManager.playVideo(
                                            context = requireContext(),
                                            bvid = videoInfo?.data?.bvid,
                                            cid = videoInfo?.data?.cid,
                                            title = videoInfo?.data?.title,
                                            progress = viewModel.historyProgress ?: 0L
                                            ?: 0,
                                            subtitleUrl = if (videoInfo?.data?.subtitle?.list.isNullOrEmpty()) null else videoInfo?.data?.subtitle?.list?.get(
                                                0
                                            )?.subtitleUrl
                                        )
                                    }
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.ThumbUp,
                                            "点赞",
                                            "点赞"
                                        ),
                                        onLongClick = { },
                                        tint = likeColor,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        videoInfo?.let { video ->
                                            viewModel.likeVideo(
                                                video.data.bvid,
                                                isLiked ?: true
                                            )
                                        }
                                    }
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.MonetizationOn,
                                            "投币",
                                            "投币"
                                        ),
                                        onLongClick = { },
                                        tint = coinColor,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        startActivity(
                                            Intent(
                                                requireActivity(),
                                                CoinActivity::class.java
                                            ).apply {
                                                putExtra("bvid", videoInfo?.data?.bvid)
                                                putExtra("coinCount", viewModel.coinCount)
                                                putExtra("aid", videoInfo?.data?.aid)
                                            })
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.StarBorder,
                                            "收藏",
                                            "收藏"
                                        ),
                                        onLongClick = { },
                                        tint = favColor,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (UserManager.isLoggedIn()) {
                                            startActivity(
                                                Intent(
                                                    requireContext(),
                                                    FavoriteFolderActivity::class.java
                                                ).apply { putExtra("aid", videoInfo?.data?.aid) })
                                        } else {
                                            ToastUtils.showText("你还没有登录捏")
                                        }
                                    }
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.History,
                                            "稍后再看",
                                            "稍后再看"
                                        ),
                                        onLongClick = { },
                                        tint = Color.White,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        videoInfo?.data?.bvid?.let { bvid ->
                                            viewModel.addToViewLater(bvid)
                                        }
                                    }
                                    RoundButton(
                                        buttonItem = RoundButtonDataNew(
                                            Icons.Outlined.SendToMobile,
                                            "手机观看",
                                            "手机观看"
                                        ),
                                        onLongClick = { /*TODO*/ },
                                        tint = Color.White,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (isAdded) {
                                            startActivity(
                                                Intent(
                                                    requireContext(),
                                                    QrCodeActivityNew::class.java
                                                ).apply {
                                                    putExtra(
                                                        PARAM_QRCODE_URL,
                                                        "https://www.bilibili.com/video/${activity.videoId}"
                                                    )
                                                    putExtra(PARAM_QRCODE_MESSAGE, "手机扫描二维码\n继续观看")
                                                })
                                        }
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    RoundButton(
                                        buttonItem = RoundButtonData(
                                            R.drawable.cloud_download,
                                            "缓存",
                                            "缓存"
                                        ),
                                        onLongClick = { },
                                        tint = Color.White,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Intent(
                                            requireActivity(),
                                            NewVideoCacheActivity::class.java
                                        ).apply {
                                            putExtra("bvid", videoInfo?.data?.bvid ?: "")
                                            putExtra("cid", videoInfo?.data?.cid ?: 0)
                                            putExtra("title", videoInfo?.data?.title)
                                            putExtra("coverUrl", videoInfo?.data?.pic)
                                            if (!videoInfo?.data?.subtitle?.list.isNullOrEmpty()) {
                                                putExtra(
                                                    "subtitleUrl",
                                                    videoInfo?.data?.subtitle?.list?.get(
                                                        0
                                                    )?.subtitleUrl
                                                )
                                            }

                                            putExtra(
                                                "data",
                                                Gson().toJson(videoInfo?.data?.pages?.let { pages ->
                                                    cn.spacexc.wearbili.dataclass.videoDetail.Data.Pages(
                                                        pages
                                                    )
                                                })
                                            )
                                            startActivity(this)
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.weight(1f))
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
                                            viewModel.getVideoInfo(activity.videoId)
                                            viewModel.getProgress(activity.videoId)
                                        },
                                    horizontalAlignment = CenterHorizontally
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
                                    horizontalAlignment = CenterHorizontally
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
                fontSize = 10.sp,
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
            Text(
                text = content,
                fontSize = 10.sp,
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