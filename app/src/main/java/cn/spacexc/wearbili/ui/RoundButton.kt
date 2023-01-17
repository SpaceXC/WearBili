package cn.spacexc.wearbili.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.RoundButtonDataNew

/**
 * Created by XC-Qan on 2023/1/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Composable
fun RoundButton(
    buttonItem: RoundButtonDataNew,
    //videoInfo: VideoDetailInfo?,
    onLongClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val localDensity = LocalDensity.current
    var buttonItemHeight by remember {
        mutableStateOf(0.dp)
    }
    Column(
        modifier = modifier
            //.fillMaxWidth()
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
fun RoundButton(
    buttonItem: RoundButtonData,
    //videoInfo: VideoDetailInfo?,
    onLongClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val localDensity = LocalDensity.current
    var buttonItemHeight by remember {
        mutableStateOf(0.dp)
    }
    Column(
        modifier = modifier
            //.fillMaxWidth()
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
                    .clip(CircleShape/*RoundedCornerShape(6.dp)*/)
                    .border(
                        width = 0.1.dp, color = Color(
                            91, 92, 93, 204
                        ), shape = CircleShape/*RoundedCornerShape(6.dp)*/
                    )
                    .background(Color(41, 41, 41, 204))
            ) {
                Icon(
                    painter = painterResource(id = buttonItem.resId),
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