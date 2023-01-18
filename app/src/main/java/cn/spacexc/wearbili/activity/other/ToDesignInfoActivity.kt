package cn.spacexc.wearbili.activity.other

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SupervisedUserCircle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.OFFICIAL_TYPE_NONE
import cn.spacexc.wearbili.ui.UserCard
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ifNullOrEmpty
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/*
WearBili  Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/11/19.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ToDesignInfoActivity : AppCompatActivity() {
    private val toDesignUser = MutableLiveData<User>()
    private val garyUser = MutableLiveData<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUser(198338518L) {
            toDesignUser.value = it
        }
        getUser(241603766L) {
            garyUser.value = it
        }
        setContent {
            val toDesign by toDesignUser.observeAsState()
            val jesseGary by garyUser.observeAsState()
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "",
                onBack = ::finish,
                isLoading = toDesign == null || jesseGary == null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val localDensity = LocalDensity.current
                    var iconSize by remember {
                        mutableStateOf(0.dp)
                    }
                    Text(
                        text = "组织详情",
                        fontFamily = puhuiFamily,
                        fontSize = 10.sp,
                        color = Color(255, 255, 255, 204),
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(id = cn.spacexc.wearbili.R.drawable.todesign_banner),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = "一个专注于设计的小小工作室", fontFamily = puhuiFamily, color = Color.White)
                    UserCard(
                        name = toDesign?.data?.name ?: "",
                        uid = 198338518L,
                        sign = "组织首页",
                        avatar = toDesign?.data?.face ?: "",
                        nicknameColor = toDesign?.data?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" },
                        officialType = toDesign?.data?.official?.type ?: OFFICIAL_TYPE_NONE,
                        context = this@ToDesignInfoActivity
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.SupervisedUserCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "参与本项目的成员",
                            color = Color.White,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.onGloballyPositioned {
                                iconSize = with(localDensity) { it.size.height.toDp() }
                            }
                        )
                    }
                    UserCard(
                        name = jesseGary?.data?.name ?: "",
                        uid = 241603766L,
                        sign = "UI设计",
                        nicknameColor = toDesign?.data?.vip?.nickname_color.ifNullOrEmpty { "#FFFFFF" },
                        avatar = jesseGary?.data?.face ?: "",
                        officialType = jesseGary?.data?.official?.type ?: OFFICIAL_TYPE_NONE,
                        context = this@ToDesignInfoActivity
                    )
                }
            }
        }
    }

    private fun getUser(mid: Long, callback: (User) -> Unit) {
        UserManager.getUserById(mid, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val user = Gson().fromJson(response.body?.string(), User::class.java)
                MainScope().launch {
                    callback(user)
                }
            }

        })
    }
}