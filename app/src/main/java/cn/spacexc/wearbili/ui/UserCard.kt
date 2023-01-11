package cn.spacexc.wearbili.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import coil.compose.AsyncImage
import coil.request.ImageRequest

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

const val OFFICIAL_TYPE_NONE = -1
const val OFFICIAL_TYPE_PERSONAL = 0
const val OFFICIAL_TYPE_ORG = 1

@Composable
fun UserCard(
    name: String,
    uid: Long,
    sign: String,
    avatar: String,
    pendant: String = "",
    nicknameColor: String = "#FFFFFF",
    officialType: Int = OFFICIAL_TYPE_NONE,
    context: Context = Application.context!!
) {
    val localDensity = LocalDensity.current
    var pendentHeight by remember {
        mutableStateOf(0.dp)
    }
    var avatarBoxSize by remember {
        mutableStateOf(0.dp)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickVfx {
                Intent(context, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", uid)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }
            }
            .border(
                width = 0.1f.dp,
                color = Color(112, 112, 112, 70),
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = Color(36, 36, 36, 100)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp, horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(2f)
                        .onGloballyPositioned {
                            avatarBoxSize = with(localDensity) { it.size.height.toDp() }
                        }
                ) {
                    if (pendant.isEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatar)
                                .placeholder(R.drawable.akari).crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(CircleShape)
                                .aspectRatio(1f)

                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatar)
                                .crossfade(true).placeholder(R.drawable.akari)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                //.fillMaxWidth()
                                .size(
                                    pendentHeight.times(0.6f),
                                    pendentHeight.times(0.6f)
                                )
                                .clip(CircleShape)
                                .aspectRatio(1f)
                        )
                        AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                            .data(pendant)
                            .crossfade(true)
                            .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    pendentHeight = with(localDensity) {
                                        it.size.height.toDp()
                                    }
                                }
                                .aspectRatio(1f)
                        )

                    }
                    if (officialType == OFFICIAL_TYPE_ORG || officialType == OFFICIAL_TYPE_PERSONAL) {
                        Image(
                            painter = painterResource(
                                id = when (officialType) {
                                    OFFICIAL_TYPE_ORG -> R.drawable.flash_blue
                                    OFFICIAL_TYPE_PERSONAL -> R.drawable.flash_yellow
                                    else -> 0
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(avatarBoxSize.times(0.2f))
                                .align(Alignment.BottomEnd)
                                .offset(
                                    x = avatarBoxSize.times(-0.15f),
                                    y = avatarBoxSize.times(-0.15f)
                                )
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = name,
                        fontFamily = puhuiFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(nicknameColor)),
                        maxLines = 1
                        //modifier = Modifier.scale(collapsingState.toolbarState.progress)
                    )
                    if (sign.isNotEmpty()) {
                        Text(
                            text = sign,
                            color = Color.White,
                            modifier = Modifier
                                .alpha(0.8f)
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 300
                                    )
                                ),
                            maxLines = 2,
                            fontFamily = puhuiFamily,
                            fontSize = 10.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}