package cn.spacexc.wearbili.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeTextDefaults
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.manager.isRound
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx

/**
 * Created by Xiaochang on 2022/9/16.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
object CirclesBackground {
    @Composable
    fun RegularBackgroundWithNoTitle(content: @Composable () -> Unit) {
        var bottomWidth by remember { mutableStateOf(0.dp) }
        var topWidth by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Box(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.circle_left_x05_cropped),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(75f)
                        .align(Alignment.Bottom)
                        .height(bottomWidth)
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            bottomWidth = with(localDensity) { it.size.width.toDp() }
                        },
                    contentScale = ContentScale.FillWidth
                )
                Spacer(Modifier.weight(25f))
            }
            Row(
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxSize()
            ) {
                Spacer(Modifier.weight(25f))
                Image(
                    painter = painterResource(id = R.drawable.circle_right_x05_cropped),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(75f)
                        .align(Alignment.Top)
                        .height(topWidth)
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            topWidth = with(localDensity) { it.size.width.toDp() }
                        },
                    contentScale = ContentScale.Fit

                )
            }
            Column(Modifier.fillMaxSize()) {
                Spacer(Modifier.width(8.dp))
                content()
                Spacer(Modifier.width(8.dp))
            }
        }
    }

    @Composable
    fun RegularBackgroundWithTitleAndBackArrow(
        title: String,
        onBack: () -> Unit = { },
        isLoading: Boolean = false,
        isError: Boolean = false,
        errorRetry: () -> Unit = {},
        contentHeight: (Dp) -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val timeSource = TimeTextDefaults.timeSource("HH:mm")
        val timeText = timeSource.currentTime
        var bottomWidth by remember { mutableStateOf(0.dp) }
        var topWidth by remember { mutableStateOf(0.dp) }
        var textHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Box(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.circle_left_x05_cropped),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(75f)
                            .align(Alignment.Bottom)
                            .height(bottomWidth)
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                bottomWidth = with(localDensity) { it.size.width.toDp() }
                            }
                            .alpha(0.78f),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(Modifier.weight(25f))
                }
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.weight(25f))
                    Image(
                        painter = painterResource(id = R.drawable.circle_right_x05_cropped),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(75f)
                            .align(Alignment.Top)
                            .height(topWidth)
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                topWidth = with(localDensity) { it.size.width.toDp() }
                            }
                            .alpha(0.78f),
                        contentScale = ContentScale.Fit

                    )
                }
            }   //背景
            Crossfade(targetState = isLoading) {
                if (!it) {
                    Column(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(6.dp))
                            if (isRound()) {
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    fontFamily = puhuiFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .onGloballyPositioned {
                                            textHeight = with(localDensity) {
                                                it.size.height.toDp()
                                            }
                                        }
                                        .align(Alignment.CenterHorizontally)
                                        .clickable(
                                            onClick = {
                                                onBack()
                                            },
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        )
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(start = 7.5f.dp, end = 7.5f.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable(
                                            onClick = {
                                                onBack()
                                            },
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBackIos,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(textHeight.times(0.7f))
                                                .align(Alignment.CenterVertically)
                                                .offset(y = (0.5).dp),
                                            tint = Color.White
                                        )
                                        //Spacer(Modifier.width(2.dp))
                                        Text(
                                            text = title,
                                            fontSize = 11.sp,
                                            fontFamily = puhuiFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .onGloballyPositioned {
                                                    textHeight = with(localDensity) {
                                                        it.size.height.toDp()
                                                    }
                                                }
                                                .align(Alignment.CenterVertically)
                                        )

                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = timeText,
                                            fontSize = 11.sp,
                                            fontFamily = googleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                        }   //标题栏
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                contentHeight(with(localDensity) { it.size.height.toDp() })
                            }) {
                            content()
                        }
                    }   //内容
                } else {
                    Crossfade(targetState = isError) { error ->
                        if (error) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Center)
                                        .clickVfx {
                                            errorRetry()
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.loading_2233_error),
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "加载失败了, 点击重试",
                                        color = Color.White,
                                        fontFamily = puhuiFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else Box(modifier = Modifier.fillMaxSize()) {
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
    }   //Compose选手：77lines

}

@Preview
@Composable
fun Preview() {
    CirclesBackground.RegularBackgroundWithNoTitle {

    }
}
