package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private val buttonList = listOf(
        RoundButtonData(R.drawable.ic_outline_home_24, "首页"),
        RoundButtonData(R.drawable.ic_baseline_person_outline_24, "我的"),
        RoundButtonData(R.drawable.mode_fan, "动态"),
        RoundButtonData(R.drawable.ic_baseline_search_24, "搜索"),
        RoundButtonData(R.drawable.ic_outline_local_fire_department_24, "热门"),
        RoundButtonData(R.drawable.ic_baseline_movie_24, "番剧"),
        RoundButtonData(R.drawable.ic_outline_tv_24, "影视"),
        RoundButtonData(R.drawable.ic_outline_info_24, "关于")
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
                override fun onClick(buttonName: String) {
                    when (buttonName) {
                        "首页" -> {
                            MainActivity.currentPageId.value = R.id.recommendFragment; finish()
                        }
                        "我的" -> {
                            MainActivity.currentPageId.value = R.id.profileFragment; finish()
                        }
                        "搜索" -> {
                            MainActivity.currentPageId.value = R.id.searchFragment; finish()
                        }
                        "关于" -> {
                            val intent = Intent(this@MenuActivity, AboutActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }

            }).also {
                it.submitList(buttonList)
            }

        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }
}