package cn.spacexc.wearbili.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
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
    contentDescription: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 0.1f.dp,
                color = Color(112, 112, 112, 70),
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = Color(36, 36, 36, 100))
            .padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
    ) {
        val localDensity = LocalDensity.current
        var posterInfoHeight by remember {
            mutableStateOf(0.dp)
        }
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(posterAvatar)
                    .crossfade(true).build(), contentDescription = null,
                modifier = Modifier.size(posterInfoHeight)
            )
            Column(modifier = Modifier.onGloballyPositioned {
                posterInfoHeight = with(localDensity) {
                    it.size.height.toDp()
                }
            }) {
                Text(
                    text = posterName,
                    color = posterNameColor,
                    fontFamily = puhuiFamily,
                    fontSize = 15.sp
                )
                Text(
                    text = postTime,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}