package cn.spacexc.wearbili.ui

import android.content.res.Configuration.UI_MODE_TYPE_WATCH
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.spacexc.wearbili.R

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
            Row(Modifier.align(Alignment.BottomStart).fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.circle_left_x05_cropped),
                    contentDescription = null,
                    modifier = Modifier.weight(75f).align(Alignment.Bottom).height(bottomWidth).fillMaxWidth()
                        .onGloballyPositioned {
                            bottomWidth = with(localDensity) { it.size.width.toDp() }
                        },
                    contentScale = ContentScale.FillWidth
                )
                Spacer(Modifier.weight(25f))
            }
            Row(Modifier.align(Alignment.TopEnd).fillMaxSize()) {
                Spacer(Modifier.weight(25f))
                Image(
                    painter = painterResource(id = R.drawable.circle_right_x05_cropped),
                    contentDescription = null,
                    modifier = Modifier.weight(75f).align(Alignment.Top).height(topWidth).fillMaxWidth()
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
    fun RegularBackgroundWithTitleAndBackArrow(title: String, onBack: () -> Unit = {}, content: @Composable () -> Unit) {
        var bottomWidth by remember { mutableStateOf(0.dp) }
        var topWidth by remember { mutableStateOf(0.dp) }
        var textHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current
        Box(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()){
                Row(Modifier.align(Alignment.BottomStart).fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.circle_left_x05_cropped),
                        contentDescription = null,
                        modifier = Modifier.weight(75f).align(Alignment.Bottom).height(bottomWidth).fillMaxWidth()
                            .onGloballyPositioned {
                                bottomWidth = with(localDensity) { it.size.width.toDp() }
                            }.alpha(0.78f),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(Modifier.weight(25f))
                }
                Row(Modifier.align(Alignment.TopEnd).fillMaxSize()) {
                    Spacer(Modifier.weight(25f))
                    Image(
                        painter = painterResource(id = R.drawable.circle_right_x05_cropped),
                        contentDescription = null,
                        modifier = Modifier.weight(75f).align(Alignment.Top).height(topWidth).fillMaxWidth()
                            .onGloballyPositioned {
                                topWidth = with(localDensity) { it.size.width.toDp() }
                            }.alpha(0.78f),
                        contentScale = ContentScale.Fit

                    )
                }
            }   //背景
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Spacer(Modifier.width(7.5f.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = {
                                onBack()
                            }, interactionSource = remember { MutableInteractionSource() }, indication = null)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = null,
                                modifier = Modifier.height(16.dp).width(16.dp).align(Alignment.CenterVertically)
                                    .offset(y = 0.9f.dp),
                                tint = Color.White
                            )
                            //Spacer(Modifier.width(2.dp))
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontFamily = puhuiFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.onGloballyPositioned {
                                    textHeight = with(localDensity) {
                                        it.size.height.toDp()
                                    }
                                }.align(Alignment.CenterVertically)
                            )
                        }
                        Spacer(Modifier.width(7.5f.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                }   //标题栏
                content()
            }   //内容
        }
    }   //Compose选手：77lines


}

@Preview(uiMode = UI_MODE_TYPE_WATCH, showSystemUi = true)
@Composable
fun Preview() {
    CirclesBackground.RegularBackgroundWithNoTitle {

    }
}
