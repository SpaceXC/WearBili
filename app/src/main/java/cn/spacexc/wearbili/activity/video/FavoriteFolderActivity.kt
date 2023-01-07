package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.viewmodel.FavoriteViewModel

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class FavoriteFolderActivity : AppCompatActivity() {
    val viewModel by viewModels<FavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aid = intent.getLongExtra("aid", 0L)
        viewModel.getFavList(aid)
        setContent {
            val localDensity = LocalDensity.current
            var buttonHeight by remember {
                mutableStateOf(0.dp)
            }
            val favList by viewModel.favList.observeAsState(emptyList())
            var idsToAdd by remember {
                mutableStateOf(listOf<Long>())
            }
            var idsToDelete by remember {
                mutableStateOf(listOf<Long>())
            }
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "收藏视频",
                onBack = ::finish,
                isLoading = favList.isEmpty()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 4.dp,
                            bottom = buttonHeight.plus(16.dp)
                        )
                    ) {
                        favList.forEach {
                            item {
                                val borderColor by animateColorAsState(
                                    targetValue = if (it.fav_state == 0) {
                                        if (idsToAdd.contains(
                                                it.id
                                            )
                                        ) Color(254, 103, 154, 204) else Color(112, 112, 112, 204)
                                    } else {
                                        if (!(idsToDelete.contains(
                                                it.id
                                            ))
                                        ) Color(254, 103, 154, 204) else Color(112, 112, 112, 204)
                                    }
                                )
                                val textColor by animateColorAsState(
                                    targetValue = if (it.fav_state == 0) {
                                        if (idsToAdd.contains(
                                                it.id
                                            )
                                        ) BilibiliPink else Color.White
                                    } else {
                                        if (!(idsToDelete.contains(
                                                it.id
                                            ))
                                        ) BilibiliPink else Color.White
                                    }
                                )
                                Column(modifier = Modifier.clickVfx {
                                    if (it.fav_state == 0) {
                                        val temp = idsToAdd.toMutableList()
                                        if (temp.contains(it.id)) {
                                            temp.remove(it.id)
                                        } else {
                                            temp.add(it.id)
                                        }
                                        idsToAdd = temp.toList()
                                    } else if (it.fav_state == 1) {
                                        val temp = idsToDelete.toMutableList()
                                        if (temp.contains(it.id)) {
                                            temp.remove(it.id)
                                        } else {
                                            temp.add(it.id)
                                        }
                                        idsToDelete = temp.toList()
                                    }
                                }) {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                width = 1f.dp,
                                                color = borderColor,
                                                shape = RoundedCornerShape(10.dp)
                                            )

                                            .background(color = Color(36, 36, 36, 199))
                                            .padding(vertical = 12.dp, horizontal = 16.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        Text(
                                            text = it.title,
                                            color = textColor,
                                            fontSize = 16.sp,
                                            fontFamily = puhuiFamily,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.alpha(0.76f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    ) {
                        AnimatedVisibility(
                            visible = idsToAdd.isNotEmpty() || idsToDelete.isNotEmpty(),
                            enter = slideInVertically {
                                it / 2
                            },
                            exit = slideOutVertically {
                                it / 2
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .onGloballyPositioned {
                                        buttonHeight = with(localDensity) { it.size.height.toDp() }
                                    }
                                    .clickVfx {
                                        viewModel.commitFav(aid, idsToAdd, idsToDelete) {
                                            if (it) finish()
                                        }
                                    }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color = Color(30, 30, 30, 230))
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                                //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
                            ) {
                                Text(
                                    text = "确定",
                                    color = Color.White,
                                    fontFamily = puhuiFamily,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Button(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {

    }
}