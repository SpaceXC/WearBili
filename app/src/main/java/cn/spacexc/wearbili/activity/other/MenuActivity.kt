package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.activity.search.SearchActivity
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MenuActivity : AppCompatActivity() {
    private val buttonList = listOf(
        RoundButtonData(R.drawable.ic_outline_home_24, "首页", "首页"),
        RoundButtonData(R.drawable.ic_baseline_person_outline_24, "我的", "我的"),
        RoundButtonData(R.drawable.mode_fan, "动态", "动态"),
        RoundButtonData(R.drawable.ic_baseline_search_24, "搜索", "搜索"),
        RoundButtonData(R.drawable.ic_baseline_search_24, "测试", "test"),
        //RoundButtonData(R.drawable.ic_outline_local_fire_department_24, "热门"),
        //RoundButtonData(R.drawable.ic_baseline_movie_24, "番剧"),
        //RoundButtonData(R.drawable.ic_outline_tv_24, "影视"),
        RoundButtonData(R.drawable.ic_outline_info_24, "关于", "关于")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        //activity = intent.getParcelableExtra("this")
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2).also {
            it.orientation = GridLayoutManager.VERTICAL
        }
        recyclerView.adapter =
            ButtonsAdapter(false, object : OnItemViewClickListener {
                override fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                    when (buttonName) {
                        "首页" -> {
                            MainActivity.currentPageId.value =
                                R.id.recommendFragment; finish(); overridePendingTransition(
                                R.anim.activity_in_y,
                                R.anim.activity_out_y
                            )
                        }
                        "动态" -> {
                            MainActivity.currentPageId.value =
                                R.id.dynamicFragment; finish(); overridePendingTransition(
                                R.anim.activity_in_y,
                                R.anim.activity_out_y
                            )
                        }
                        "我的" -> {
                            MainActivity.currentPageId.value =
                                R.id.profileFragment; finish(); overridePendingTransition(
                                R.anim.activity_in_y,
                                R.anim.activity_out_y
                            )
                        }
                        "搜索" -> {
                            val intent = Intent(
                                this@MenuActivity,
                                SearchActivity::class.java
                            ); overridePendingTransition(
                                R.anim.activity_in_y,
                                R.anim.activity_out_y
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.activity_in_y, R.anim.activity_out_y)
                        }
                        "关于" -> {
                            val intent = Intent(this@MenuActivity, AboutActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.activity_in_y, R.anim.activity_out_y)
                        }
                        "测试" -> {
                            val data: Uri =
                                Uri.parse("bilibili://video/428340116?player_height=1080&player_rotate=0&player_width=1920")
                            val intent = Intent(Intent.ACTION_VIEW, data)
                            //保证新启动的APP有单独的堆栈，如果希望新启动的APP和原有APP使用同一个堆栈则去掉该项
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ToastUtils.makeText(
                                    "没有匹配的APP，请下载安装"
                                ).show()
                            }
                        }
                    }
                }

                override fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {

                }

            }).also {
                it.submitList(buttonList)
            }
        findViewById<TextView>(R.id.pageName).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.activity_in_y, R.anim.activity_out_y)
        }

        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }
}