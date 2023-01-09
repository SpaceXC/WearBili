package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.ui.DynamicCard
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.TimeUtils.toDateStr
import cn.spacexc.wearbili.viewmodel.DynamicViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/*
 * Created by XC on 2022/11/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DynamicListFragment : Fragment() {
    val viewModel by viewModels<DynamicViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return ComposeView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ComposeView).setContent {
            val dynamicList by viewModel.dynamicCardList.observeAsState()
            val isError by viewModel.isError.observeAsState()
            val refreshState = rememberSwipeRefreshState(
                isRefreshing = viewModel.isRefreshing.observeAsState().value ?: false
            )
            if (UserManager.isLoggedIn()) {
                Crossfade(targetState = !dynamicList.isNullOrEmpty()) {
                    if (it) {
                        SwipeRefresh(state = refreshState, onRefresh = { viewModel.getDynamic() }) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = viewModel.lazyListState
                            ) {
                                dynamicList?.forEach { card ->
                                    item {
                                        DynamicCard(
                                            posterAvatar = card.desc.user_profile.info.face,
                                            posterName = card.desc.user_profile.info.uname,
                                            posterNameColor = if (!card.desc.user_profile.vip.nickname_color.isNullOrEmpty()) Color(
                                                android.graphics.Color.parseColor(
                                                    card.desc.user_profile.vip.nickname_color
                                                )
                                            ) else Color.White,
                                            postTime = (card.desc.timestamp * 1000).toDateStr("MM-dd HH:mm"),
                                            card = card,
                                            context = context
                                        )
                                    }
                                }
                                item {
                                    LaunchedEffect(key1 = Unit) {
                                        viewModel.getMoreDynamic()
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
                                                refresh()
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.empty),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "你还没有登录啊",
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

    override fun onResume() {
        super.onResume()
        if (viewModel.dynamicCardList.value.isNullOrEmpty()) {
            viewModel.getDynamic()
        }
    }

    fun refresh() {
        viewModel.getDynamic()
    }
}