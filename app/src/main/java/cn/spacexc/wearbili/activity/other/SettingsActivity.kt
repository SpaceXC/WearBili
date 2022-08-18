package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.SettingsAdapter
import cn.spacexc.wearbili.dataclass.settings.SettingItem
import cn.spacexc.wearbili.dataclass.settings.SettingType
import cn.spacexc.wearbili.manager.CookiesManager
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private val settingsList = listOf(
        SettingItem(
            type = SettingType.TYPE_ACTION,
            settingName = "logOut",
            displayName = "登出",
            iconRes = R.drawable.logout,
            requireSave = false,
            color = "#FE679A",
            description = "登出当前的账号",
            action = {
                UserManager.logout(object : NetworkUtils.ResultCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        MainScope().launch {
                            if (result) {
                                CookiesManager.deleteAllCookies()
                                ToastUtils.showText("登出成功")
                                Intent(
                                    this@SettingsActivity,
                                    SplashScreenActivity::class.java
                                ).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(this)
                                }
                            } else {
                                ToastUtils.showText("网络异常")
                            }
                        }
                    }

                    override fun onFailed(e: Exception) {
                        MainScope().launch {
                            ToastUtils.showText("网络异常")
                        }
                    }

                })
            }
        )
    )

    val adapter = SettingsAdapter()
    lateinit var pageName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter.apply { submitList(settingsList) }
        pageName = findViewById(R.id.pageName)
        val timeText = findViewById<TextView>(R.id.timeText)
        pageName.setOnClickListener { finish() }
        lifecycleScope.launchWhenCreated {
            while (true) {
                timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }
}