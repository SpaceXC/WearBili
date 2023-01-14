package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.dynamic.DynamicDetailActivity
import cn.spacexc.wearbili.activity.image.ImageViewerActivity
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DrawItem
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DynamicItem
import cn.spacexc.wearbili.dataclass.dynamic.new.list.ItemRichTextNode
import cn.spacexc.wearbili.dataclass.dynamic.new.list.Orig
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import cn.spacexc.wearbili.utils.parseColor
import coil.compose.AsyncImage
import coil.request.ImageRequest

/*
 * Created by XC on 2022/11/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Composable
fun RichText(textNodes: List<ItemRichTextNode>, fontSize: TextUnit) {
    val inlineContentMap = hashMapOf(
        "webLinkIcon" to InlineTextContent(
            Placeholder(
                width = fontSize,
                height = fontSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = parseColor("#178bcf"),
                modifier = Modifier.fillMaxSize()
            )
        }
    )
    val annotatedString = buildAnnotatedString {
        textNodes.forEach {
            when (it.type) {
                "RICH_TEXT_NODE_TYPE_TEXT" -> {
                    append(it.text)
                }
                "RICH_TEXT_NODE_TYPE_WEB" -> {
                    withStyle(style = SpanStyle(color = parseColor("#178bcf"))) {
                        appendInlineContent("webLinkIcon")
                        append("网页链接")
                    }
                }
                "RICH_TEXT_NODE_TYPE_TOPIC" -> {
                    withStyle(style = SpanStyle(color = parseColor("#178bcf"))) {
                        append(it.text)
                    }
                }
                "RICH_TEXT_NODE_TYPE_EMOJI" -> {
                    appendInlineContent(it.emoji?.text ?: "")
                    inlineContentMap[it.emoji?.text ?: ""] = InlineTextContent(
                        Placeholder(
                            width = fontSize.times(1.4f),
                            height = fontSize.times(1.4f),
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) { _ ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.emoji?.iconUrl)/*.size(
                                with(localDensity) { fontSize.roundToPx() }
                            )*/.crossfade(true).build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> {
                    append(it.text)
                }
            }
        }
    }
    Text(
        text = annotatedString,
        fontFamily = puhuiFamily,
        color = Color.White,
        fontSize = fontSize,
        inlineContent = inlineContentMap
    )
}

@Composable
fun DynamicCardNew(
    item: DynamicItem,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickVfx {
                val intent = Intent(context, DynamicDetailActivity::class.java)
                intent.putExtra("dynamicId", item.idStr.toLong())
                context.startActivity(intent)
            }
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 0.1f.dp,
                color = Color(112, 112, 112, 70),
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = Color(36, 36, 36, 100))
            .padding(start = 8.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),
    ) {
        val localDensity = LocalDensity.current
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickVfx {
                Intent(context, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", item.modules.moduleAuthor.mid)
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
                if (item.modules.moduleAuthor.pendant.imageEnhance.isEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .size(avatarSizePx.times(0.9f).toInt())
                            .data(item.modules.moduleAuthor.face)
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
                            .data(item.modules.moduleAuthor.face)
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
                    )
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                        .size(avatarSizePx.times(1.4f).toInt())
                        .data(item.modules.moduleAuthor.pendant.imageEnhance)
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
                if (item.modules.moduleAuthor.officialVerify.type == OFFICIAL_TYPE_ORG || item.modules.moduleAuthor.officialVerify.type == OFFICIAL_TYPE_PERSONAL) {
                    Image(
                        painter = painterResource(
                            id = when (item.modules.moduleAuthor.officialVerify.type) {
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
                                x = if (item.modules.moduleAuthor.pendant.imageEnhance.isEmpty()) 0.dp else avatarBoxSize.times(
                                    -0.23f
                                ),
                                y = if (item.modules.moduleAuthor.pendant.imageEnhance.isEmpty()) 0.dp else avatarBoxSize.times(
                                    -0.23f
                                )
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.onGloballyPositioned {
                avatarBoxSize = with(localDensity) { it.size.height.toDp() }
            }) {

                Text(
                    text = item.modules.moduleAuthor.name,
                    fontFamily = puhuiFamily,
                    fontSize = 12.sp,
                    color = parseColor(item.modules.moduleAuthor.vip.nicknameColor.ifNullOrEmpty { "#FFFFFF" }),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.modules.moduleAuthor.pubTime} ${item.modules.moduleAuthor.pubAction}",
                    fontFamily = puhuiFamily,
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(0.6f)
                )
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        when (item.type) {
            "DYNAMIC_TYPE_FORWARD" -> {
                item.modules.moduleDynamic.desc?.let {
                    RichText(textNodes = it.richTextNodes, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (item.orig != null) {
                    ForwardShareDynamicCardNew(
                        item = item.orig,
                        context = context,
                        dyId = item.orig.idStr.toLong(),
                        hasPoster = item.orig.type != "DYNAMIC_TYPE_PGC"
                    )
                }
            }
            "DYNAMIC_TYPE_DRAW" -> {
                val imageList = item.modules.moduleDynamic.major?.draw?.items
                item.modules.moduleDynamic.desc?.let {
                    RichText(textNodes = it.richTextNodes, fontSize = 12.sp)
                }
                if (!imageList.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyVerticalGrid(
                        modifier = Modifier.requiredSizeIn(maxHeight = 4000.dp),
                        columns = GridCells.Fixed(
                            when (imageList.size) {
                                1 -> 1
                                2 -> 2
                                else -> 3
                            }
                        )
                    ) {
                        imageList.forEachIndexed { index, image ->
                            item {
                                AsyncImage(
                                    model = ImageRequest.Builder(
                                        LocalContext.current
                                    ).data(image.src).crossfade(true)
                                        .placeholder(R.drawable.placeholder)
                                        .build(),
                                    contentDescription = null,
                                    modifier = when (imageList.size) {
                                        1 -> Modifier
                                            .fillMaxWidth()
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<DrawItem>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context.startActivity(intent)
                                            }
                                            .aspectRatio(image.width.toFloat() / image.height.toFloat())
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                        else -> Modifier
                                            .padding(2.dp)
                                            .aspectRatio(1f)
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<DrawItem>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context.startActivity(intent)
                                            }
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                    },
                                    contentScale = if (imageList.size == 1) ContentScale.FillBounds else ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
            "DYNAMIC_TYPE_WORD" -> {
                item.modules.moduleDynamic.desc?.let {
                    RichText(textNodes = it.richTextNodes, fontSize = 12.sp)
                }
            }
            "DYNAMIC_TYPE_AV" -> {
                var infoHeight by remember {
                    mutableStateOf(0.dp)
                }
                item.modules.moduleDynamic.desc?.let {
                    RichText(textNodes = it.richTextNodes, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Box(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (!item.modules.moduleDynamic.major?.archive?.aid.isNullOrEmpty()) {
                                val intent = Intent(
                                    context,
                                    VideoLongClickActivity::class.java
                                )
                                intent.putExtra(
                                    "bvid",
                                    VideoUtils.av2bv("av${item.modules.moduleDynamic.major?.archive?.aid}")
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            }
                        }, onTap = {
                            if (!item.modules.moduleDynamic.major?.archive?.aid.isNullOrEmpty()) {
                                Intent(
                                    context,
                                    VideoActivity::class.java
                                ).apply {
                                    putExtra(
                                        "videoId",
                                        VideoUtils.av2bv("av${item.modules.moduleDynamic.major?.archive?.aid}")
                                    )
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(this)
                                }
                            }
                        })
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.modules.moduleDynamic.major?.archive?.cover).crossfade(true)
                            .placeholder(R.drawable.placeholder)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                            .height(infoHeight)
                            .align(Alignment.BottomCenter),
                    )   //阴影
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .onGloballyPositioned {
                                infoHeight = with(localDensity) {
                                    it.size.height
                                        .toDp()
                                }
                            }
                            .padding(6.dp)) {
                        Text(
                            text = item.modules.moduleDynamic.major?.archive?.title ?: "",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier,
                            fontSize = 11.sp
                        )
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier.size(textHeight),
                                tint = Color.White
                            )
                            Text(
                                text = item.modules.moduleDynamic.major?.archive?.stat?.play ?: "",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.onGloballyPositioned {
                                    with(localDensity) {
                                        textHeight =
                                            it.size.height.toDp()
                                    }
                                })
                        }
                    }
                }
            }
            "DYNAMIC_TYPE_PGC" -> {
                /*Text(
                    text = "${item.modules.moduleDynamic.major?.pgc?.title} 更新了",
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 12.sp
                )*/
                Column(modifier = Modifier.clickVfx {
                    Intent(
                        context,
                        BangumiActivity::class.java
                    ).apply {
                        putExtra("id", item.modules.moduleDynamic.major?.pgc?.epid.toString())
                        putExtra("idType", ID_TYPE_EPID)
                        context.startActivity(this)
                    }
                }) {
                    var infoHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.modules.moduleDynamic.major?.pgc?.cover).crossfade(true)
                                .placeholder(R.drawable.placeholder)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                                .height(infoHeight)
                                .align(Alignment.BottomCenter),
                        )   //阴影
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .onGloballyPositioned {
                                    infoHeight = with(localDensity) {
                                        it.size.height
                                            .toDp()
                                    }
                                }
                                .padding(6.dp)) {
                            Text(
                                text = item.modules.moduleDynamic.major?.pgc?.title ?: "",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier,
                                fontSize = 11.sp
                            )
                            Row {
                                var textHeight by remember {
                                    mutableStateOf(0.dp)
                                }
                                Icon(
                                    imageVector = Icons.Outlined.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(textHeight),
                                    tint = Color.White
                                )
                                Text(
                                    text = item.modules.moduleDynamic.major?.pgc?.stat?.play ?: "",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.onGloballyPositioned {
                                        with(localDensity) {
                                            textHeight =
                                                it.size.height.toDp()
                                        }
                                    })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            else -> {
                Text(
                    text = "不支持的动态类型",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = puhuiFamily
                )
            }
        }
    }
}

@Composable
fun ForwardShareDynamicCardNew(
    item: Orig,
    context: Context,
    dyId: Long,
    hasPoster: Boolean = true
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickVfx {
                val intent = Intent(context, DynamicDetailActivity::class.java)
                intent.putExtra("dynamicId", dyId)
                context.startActivity(intent)
            }
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 0.1f.dp,
                color = Color(112, 112, 112, 70),
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = Color(36, 36, 36, 100))
            .padding(start = 8.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),
    ) {
        val localDensity = LocalDensity.current
        var posterInfoHeight by remember {
            mutableStateOf(0.dp)
        }
        if (hasPoster) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.modules.moduleAuthor.face)
                        .placeholder(R.drawable.akari)
                        .crossfade(true).build(), contentDescription = null,
                    modifier = Modifier
                        .size(posterInfoHeight)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.onGloballyPositioned {
                    posterInfoHeight = with(localDensity) {
                        it.size.height.toDp()
                    }
                }) {
                    Text(
                        text = item.modules.moduleAuthor.name,
                        color = parseColor(item.modules.moduleAuthor.vip?.nicknameColor.ifNullOrEmpty { "#FFFFFF" }),
                        fontFamily = puhuiFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        when (item.type) {
            "DYNAMIC_TYPE_DRAW" -> {
                val imageList = item.modules.moduleDynamic.major?.draw?.items
                item.modules.moduleDynamic.desc?.text?.let {
                    Text(
                        text = it,
                        fontFamily = puhuiFamily,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                if (!imageList.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyVerticalGrid(
                        modifier = Modifier.requiredSizeIn(maxHeight = 4000.dp),
                        columns = GridCells.Fixed(
                            when (imageList.size) {
                                1 -> 1
                                2 -> 2
                                else -> 3
                            }
                        )
                    ) {
                        imageList.forEachIndexed { index, image ->
                            item {
                                AsyncImage(
                                    model = ImageRequest.Builder(
                                        LocalContext.current
                                    ).data(image.src).crossfade(true)
                                        .placeholder(R.drawable.placeholder)
                                        .build(),
                                    contentDescription = null,
                                    modifier = when (imageList.size) {
                                        1 -> Modifier
                                            .fillMaxWidth()
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<DrawItem>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context.startActivity(intent)
                                            }
                                            .aspectRatio(image.width.toFloat() / image.height.toFloat())
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                        else -> Modifier
                                            .padding(2.dp)
                                            .aspectRatio(1f)
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<DrawItem>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context.startActivity(intent)
                                            }
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                    },
                                    contentScale = if (imageList.size == 1) ContentScale.FillBounds else ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
            "DYNAMIC_TYPE_WORD" -> {
                Text(
                    text = item.modules.moduleDynamic.desc?.text ?: "",
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
            "DYNAMIC_TYPE_AV" -> {
                var infoHeight by remember {
                    mutableStateOf(0.dp)
                }
                item.modules.moduleDynamic.desc?.text?.let {
                    Text(
                        text = it,
                        fontFamily = puhuiFamily,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (!item.modules.moduleDynamic.major?.archive?.aid.isNullOrEmpty()) {
                                val intent = Intent(
                                    context,
                                    VideoLongClickActivity::class.java
                                )
                                intent.putExtra(
                                    "bvid",
                                    VideoUtils.av2bv("av${item.modules.moduleDynamic.major?.archive?.aid}")
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            }
                        }, onTap = {
                            if (!item.modules.moduleDynamic.major?.archive?.aid.isNullOrEmpty()) {
                                Intent(
                                    context,
                                    VideoActivity::class.java
                                ).apply {
                                    putExtra(
                                        "videoId",
                                        VideoUtils.av2bv("av${item.modules.moduleDynamic.major?.archive?.aid}")
                                    )
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(this)
                                }
                            }
                        })
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.modules.moduleDynamic.major?.archive?.cover).crossfade(true)
                            .placeholder(R.drawable.placeholder)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                            .height(infoHeight)
                            .align(Alignment.BottomCenter),
                    )   //阴影
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .onGloballyPositioned {
                                infoHeight = with(localDensity) {
                                    it.size.height
                                        .toDp()
                                }
                            }
                            .padding(6.dp)) {
                        Text(
                            text = item.modules.moduleDynamic.major?.archive?.title ?: "",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier,
                            fontSize = 11.sp
                        )
                        Row {
                            var textHeight by remember {
                                mutableStateOf(0.dp)
                            }
                            Icon(
                                imageVector = Icons.Outlined.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier.size(textHeight),
                                tint = Color.White
                            )
                            Text(
                                text = item.modules.moduleDynamic.major?.archive?.stat?.play ?: "",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.onGloballyPositioned {
                                    with(localDensity) {
                                        textHeight =
                                            it.size.height.toDp()
                                    }
                                })
                        }
                    }
                }
            }
            "DYNAMIC_TYPE_PGC" -> {
                /*Text(
                    text = "${item.modules.moduleDynamic.major?.pgc?.title} 更新了",
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 11.sp
                )*/
                Column(modifier = Modifier.clickVfx {
                    Intent(
                        context,
                        BangumiActivity::class.java
                    ).apply {
                        putExtra("id", item.modules.moduleDynamic.major?.pgc?.epid.toString())
                        putExtra("idType", ID_TYPE_EPID)
                        context.startActivity(this)
                    }
                }) {
                    var infoHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.modules.moduleDynamic.major?.pgc?.cover).crossfade(true)
                                .placeholder(R.drawable.placeholder)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                                .height(infoHeight)
                                .align(Alignment.BottomCenter),
                        )   //阴影
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .onGloballyPositioned {
                                    infoHeight = with(localDensity) {
                                        it.size.height
                                            .toDp()
                                    }
                                }
                                .padding(6.dp)) {
                            Text(
                                text = item.modules.moduleDynamic.major?.pgc?.title ?: "",
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier,
                                fontSize = 11.sp
                            )
                            Row {
                                var textHeight by remember {
                                    mutableStateOf(0.dp)
                                }
                                Icon(
                                    imageVector = Icons.Outlined.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(textHeight),
                                    tint = Color.White
                                )
                                Text(
                                    text = item.modules.moduleDynamic.major?.pgc?.stat?.play ?: "",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.onGloballyPositioned {
                                        with(localDensity) {
                                            textHeight =
                                                it.size.height.toDp()
                                        }
                                    })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            else -> {
                Text(
                    text = "不支持的动态类型",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = puhuiFamily
                )
            }
        }
    }
}