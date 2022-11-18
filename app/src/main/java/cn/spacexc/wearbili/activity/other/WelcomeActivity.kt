package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.manager.isRound
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.SharedPreferencesUtils

/*
 * Created by XC on 2022/11/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class WelcomeActivity : AppCompatActivity() {
    private val pageCurrent: MutableLiveData<Int> = MutableLiveData<Int>(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentPage by pageCurrent.observeAsState()
            CirclesBackground.RegularBackgroundWithNoTitle {
                Crossfade(
                    targetState = currentPage,
                    animationSpec = tween(durationMillis = 400)
                ) { page ->

                    when (page) {
                        1 -> FirstPage()
                        2 -> SecondPage()
                        3 -> ThirdPage()
                        4 -> ForthPage()
                        5 -> FifthPage()
                    }


                }

                /*AnimatedContent(targetState = currentPage, transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (targetState > initialState) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInHorizontally { height -> height } + fadeIn() with
                                slideOutHorizontally { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInHorizontally { height -> -height } + fadeIn() with
                                slideOutHorizontally { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }) {
                    when (currentPage) {
                        1 -> FirstPage {
                            if (it) currentPage++ else currentPage--
                        }
                        2 -> SecondPage {
                            if (it) currentPage++ else currentPage--
                        }
                    }
                }*/


            }
        }
    }

    @Composable
    fun FirstPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.mipmap.app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "欢迎使用",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "WearBili",
                color = BilibiliPink,
                fontFamily = puhuiFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    /*.border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(10.dp)
                    )*/
                    .background(color = Color(36, 36, 36, 128))
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .clickVfx { pageCurrent.value = pageCurrent.value?.plus(1) },
                horizontalAlignment = Alignment.CenterHorizontally
                //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Text(
                    text = "开始设置",
                    color = Color.White,
                    fontFamily = puhuiFamily,
                    fontSize = 16.sp
                    //fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$APP_VERSION | ${Build.DEVICE}",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 11.sp,
                modifier = Modifier
                    .alpha(0.6f)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
            )
            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
        }
    }

    @Composable
    fun SecondPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start

        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Outlined.Policy,
                contentDescription = null,
                tint = BilibiliPink,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "条款与隐私协议",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "在开始使用前，您需要阅读条款与隐私协议。",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 14.sp,
                //fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    /*.border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(10.dp)
                    )*/
                    .background(color = Color(36, 36, 36, 128))
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .clickVfx { pageCurrent.value = pageCurrent.value?.plus(1) },
                horizontalAlignment = Alignment.CenterHorizontally
                //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Text(
                    text = "阅读",
                    color = Color.White,
                    fontFamily = puhuiFamily,
                    fontSize = 16.sp
                    //fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    /*.border(
                        width = 0.1f.dp,
                        color = Color(112, 112, 112, 70),
                        shape = RoundedCornerShape(10.dp)
                    )*/
                    .background(color = Color(36, 36, 36, 128))
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .clickVfx { pageCurrent.value = pageCurrent.value?.minus(1) },
                horizontalAlignment = Alignment.CenterHorizontally
                //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Text(
                    text = "返回",
                    color = Color.White,
                    fontFamily = puhuiFamily,
                    fontSize = 16.sp
                    //fontWeight = FontWeight.Medium
                )
            }
            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
        }
    }

    @Composable
    fun ThirdPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start

        ) {
            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "WearBili条款与隐私协议",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "更新时间：2022年11月16日", color = Color.White,
                fontFamily = puhuiFamily, modifier = Modifier.alpha(0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "WearBili（以下简称“本程序”）是由个人及第三方组织开发的哔哩哔哩手表客户端。主程序XCちゃん谨代表所有参与本程序开发工作的伙伴们声明：\n" +
                        "\n一、本程序在运行过程中会收集部分用户数据，这些数据中不会包含隐私数据，这些数据包括但不限于您的设备名称、设备品牌、设备系统版本等。详情请参阅https://spacexc.cn/WearBili/CollectedData/；\n" +
                        "\n二、本程序无任何盈利行为，项目的发起完全基于开发者们对于哔哩哔哩的热爱（希望B站手下留情不要起诉我们啊求求了有什么事我们可以好好商量）。若您认为本程序中的内容侵犯到了您的合法权益，您可以编辑邮件到xcatapple@icloud.com或在程序中的问题反馈中提交您的问题；\n" +
                        "\n三、我们接受大家的监督审查。您可以在https://github.com/XC-Qan/WearBili上查看本项目部分开源代码。您也可以在即时设计官方网站或ToDesign@Bilibili账号下查看此应用的UI设计稿件。\n" +
                        "\n四、哔哩哔哩 (゜-゜)つロ 干杯\n" +
                        "\nEnjoy～", color = Color.White,
                fontFamily = puhuiFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { pageCurrent.value = pageCurrent.value?.plus(1) }, text = "我已阅读并同意")
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { finish() }, text = "不同意并退出")
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { pageCurrent.value = pageCurrent.value?.minus(1) }, text = "返回")
            Spacer(modifier = Modifier.height(4.dp))

            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
        }
    }

    @Composable
    fun ForthPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start

        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = BilibiliPink,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "登入哔哩哔哩账号",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "立即登入哔哩哔哩账号\n以获得更好的体验",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 14.sp,
                //fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                intent.putExtra("fromHome", false)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }, text = "登入")
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "跳过",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickVfx {
                        pageCurrent.value = pageCurrent.value?.plus(1)
                    }
                    .align(Alignment.CenterHorizontally)
                //fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
        }
    }

    @Composable
    fun FifthPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start

        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = BilibiliPink,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "搞定(≧∇≦)ﾉ",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "立即开始使用WearBili吧",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 14.sp,
                //fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(this@WelcomeActivity, SplashScreenActivity::class.java)
                startActivity(intent)
                SharedPreferencesUtils.saveBool("hasSetUp", true)
                overridePendingTransition(
                    R.anim.activity_fade_in,
                    R.anim.activity_fade_out
                )
                finish()
            }, text = "开始")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (UserManager.isLoggedIn()) "UID:${UserManager.getUid()}" else "未登录",
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 14.sp,
                //fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(0.8f)
            )
            if (isRound()) Spacer(modifier = Modifier.height(40.dp))
        }
    }

    override fun onResume() {
        super.onResume()
        if (UserManager.isLoggedIn()) {
            pageCurrent.value = 5
        }
    }

    @Composable
    fun Button(onClick: () -> Unit, text: String) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                /*.border(
                    width = 0.1f.dp,
                    color = Color(112, 112, 112, 70),
                    shape = RoundedCornerShape(10.dp)
                )*/
                .background(color = Color(36, 36, 36, 128))
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .clickVfx { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
            //.padding(start = 6.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
        ) {
            Text(
                text = text,
                color = Color.White,
                fontFamily = puhuiFamily,
                fontSize = 16.sp
                //fontWeight = FontWeight.Medium
            )
        }
    }
}