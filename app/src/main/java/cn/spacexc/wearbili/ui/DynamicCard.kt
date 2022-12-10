package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.bangumi.BangumiActivity
import cn.spacexc.wearbili.activity.dynamic.DynamicDetailActivity
import cn.spacexc.wearbili.activity.image.ImageViewerActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.activity.video.VideoLongClickActivity
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.dataclass.dynamic.dynamicepisode.EpisodeCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card.ForwardShareCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.ImageCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.Picture
import cn.spacexc.wearbili.dataclass.dynamic.dynamictext.card.TextCard
import cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card.VideoCard
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.utils.ifNullOrEmpty
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
fun DynamicCard(
    posterAvatar: String,
    posterName: String,
    posterNameColor: Color = Color(255, 255, 255),
    postTime: String,
    card: Card,
    context: Context?
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickVfx {
                val intent = Intent(context, DynamicDetailActivity::class.java)
                intent.putExtra("dynamicId", card.desc.dynamic_id)
                context?.startActivity(intent)
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
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(posterAvatar)
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
                    text = posterName,
                    color = posterNameColor,
                    fontFamily = puhuiFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = postTime,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f),
                    fontFamily = googleSansFamily
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        when (card.desc.type) {
            1 -> {
                val newCard = card.cardObj as ForwardShareCard
                Text(
                    text = newCard.item.content.ifNullOrEmpty { "分享动态" },
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                ForwardShareDynamicCard(
                    posterAvatar = newCard.origin_user?.info?.face ?: "",
                    posterName = newCard.origin_user?.info?.uname ?: "",
                    posterNameColor = if (!newCard.origin_user?.vip?.nickname_color.isNullOrEmpty()) Color(
                        android.graphics.Color.parseColor(
                            newCard.origin_user?.vip?.nickname_color
                        )
                    ) else Color.White,
                    card = newCard,
                    context = context,
                    dyId = card.desc.orig_dy_id!!,
                    hasPoster = card.desc.orig_type != 4098
                )
            }
            2 -> {
                val newCard = card.cardObj as ImageCard
                val imageList = newCard.item.pictures
                Text(
                    text = newCard.item.description.ifNullOrEmpty { "分享图片" },
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                if (imageList.isNotEmpty()) {
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
                                    ).data(image.img_src).crossfade(true)
                                        .placeholder(R.drawable.placeholder)
                                        .build(),
                                    contentDescription = null,
                                    modifier = when (imageList.size) {
                                        1 -> Modifier
                                            .fillMaxWidth()
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<Picture>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context?.startActivity(intent)
                                            }
                                            .aspectRatio(image.img_width.toFloat() / image.img_height.toFloat())
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                        else -> Modifier
                                            .padding(2.dp)
                                            .aspectRatio(1f)
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<Picture>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context?.startActivity(intent)
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
            4 -> {
                val newCard = card.cardObj as TextCard
                Text(
                    text = newCard.item.content,
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
            }
            8 -> {
                val newCard = card.cardObj as VideoCard
                var infoHeight by remember {
                    mutableStateOf(0.dp)
                }
                Text(
                    text = newCard.dynamic.ifNullOrEmpty { "投稿视频" },
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (newCard.aid != 0L) {
                                val intent = Intent(
                                    context,
                                    VideoLongClickActivity::class.java
                                )
                                intent.putExtra(
                                    "bvid",
                                    VideoUtils.av2bv("av${newCard.aid}")
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                context?.startActivity(intent)
                            }
                        }, onTap = {
                            if (newCard.aid != 0L) {
                                Intent(
                                    context,
                                    VideoActivity::class.java
                                ).apply {
                                    putExtra(
                                        "videoId",
                                        VideoUtils.av2bv("av${newCard.aid}")
                                    )
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    context?.startActivity(this)
                                }
                            }
                        })
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(newCard.pic).crossfade(true).placeholder(R.drawable.placeholder)
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
                            text = newCard.title,
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier,
                            fontSize = 12.sp
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
                                text = newCard.stat.view.toShortChinese(),
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
            4098 -> {
                val newCard = card.cardObj as EpisodeCard
                Text(
                    text = "${newCard.apiSeasonInfo.title} 更新了",
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                Column(modifier = Modifier.clickVfx {
                    Intent(
                        context,
                        BangumiActivity::class.java
                    ).apply {
                        putExtra("id", newCard.episode_id.toString())
                        putExtra("idType", ID_TYPE_EPID)
                        context?.startActivity(this)
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
                                .data(newCard.cover).crossfade(true)
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
                                text = newCard.index_title,
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier,
                                fontSize = 12.sp
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
                                    text = newCard.play_count.toShortChinese(),
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
        }
    }
}

@Composable
fun ForwardShareDynamicCard(
    posterAvatar: String,
    posterName: String,
    posterNameColor: Color = Color(255, 255, 255),
    card: ForwardShareCard,
    context: Context?,
    dyId: Long,
    hasPoster: Boolean = true
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickVfx {
                val intent = Intent(context, DynamicDetailActivity::class.java)
                intent.putExtra("dynamicId", dyId)
                context?.startActivity(intent)
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
                    model = ImageRequest.Builder(LocalContext.current).data(posterAvatar)
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
                        text = posterName,
                        color = posterNameColor,
                        fontFamily = puhuiFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        when (card.originObj) {
            is ForwardShareCard -> {
                val newCard = card.originObj as ForwardShareCard
                Text(
                    text = newCard.item.content.ifNullOrEmpty { "分享动态" },
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))

            }
            is ImageCard -> {
                val newCard = card.originObj as ImageCard
                val imageList = newCard.item.pictures
                Text(
                    text = newCard.item.description.ifNullOrEmpty { "分享图片" },
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 12.sp
                )
                if (imageList.isNotEmpty()) {
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
                                    ).data(image.img_src).crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = when (imageList.size) {
                                        1 -> Modifier
                                            .fillMaxWidth()
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<Picture>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context?.startActivity(intent)
                                            }
                                            .aspectRatio(image.img_width.toFloat() / image.img_height.toFloat())
                                            .clip(
                                                RoundedCornerShape(6.dp)
                                            )
                                        else -> Modifier
                                            .padding(2.dp)
                                            .aspectRatio(1f)
                                            .clickVfx {
                                                val intent =
                                                    Intent(context, ImageViewerActivity::class.java)
                                                val arrayList = arrayListOf<Picture>()
                                                arrayList.addAll(imageList)
                                                intent.putParcelableArrayListExtra(
                                                    "imageList",
                                                    arrayList
                                                )
                                                intent.putExtra("currentPhotoItem", index)
                                                context?.startActivity(intent)
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
            is TextCard -> {
                val newCard = card.originObj as TextCard
                Text(
                    text = newCard.item.content,
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            is VideoCard -> {
                val newCard = card.originObj as VideoCard
                var infoHeight by remember {
                    mutableStateOf(0.dp)
                }
                Text(
                    text = newCard.dynamic.ifNullOrEmpty { "投稿视频" },
                    fontFamily = puhuiFamily,
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
                                if (newCard.aid != 0L) {
                                    val intent = Intent(
                                        context,
                                        VideoLongClickActivity::class.java
                                    )
                                    intent.putExtra(
                                        "bvid",
                                        VideoUtils.av2bv("av${newCard.aid}")
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    context?.startActivity(intent)
                                }
                            }, onTap = {
                                if (newCard.aid != 0L) {
                                    Intent(
                                        context,
                                        VideoActivity::class.java
                                    ).apply {
                                        putExtra(
                                            "videoId",
                                            VideoUtils.av2bv("av${newCard.aid}")
                                        )
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                        context?.startActivity(this)
                                    }
                                }
                            })
                        }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(newCard.pic).placeholder(R.drawable.placeholder).crossfade(true)
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
                            text = newCard.title,
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier,
                            fontSize = 10.sp
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
                                text = newCard.stat.view.toShortChinese(),
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.onGloballyPositioned {
                                    with(localDensity) {
                                        textHeight =
                                            it.size.height.toDp()
                                    }
                                }
                            )
                        }
                    }
                }
            }
            is EpisodeCard -> {
                val newCard = card.originObj as EpisodeCard
                Text(
                    text = "${newCard.apiSeasonInfo.title} 更新了",
                    fontFamily = puhuiFamily,
                    color = Color.White
                )
                Column(modifier = Modifier.clickVfx {
                    Intent(
                        context,
                        BangumiActivity::class.java
                    ).apply {
                        putExtra("id", newCard.episode_id.toString())
                        putExtra("idType", ID_TYPE_EPID)
                        context?.startActivity(this)
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
                                .data(newCard.cover).crossfade(true)
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
                                text = newCard.index_title,
                                color = Color.White,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier,
                                fontSize = 10.sp
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
                                    text = newCard.play_count.toShortChinese(),
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
        }
    }
}