package cn.spacexc.wearbili.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.HorizontalButtonAdapter
import cn.spacexc.wearbili.adapter.UserHorizontalButtonAdapter
import cn.spacexc.wearbili.dataclass.HorizontalButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AboutActivity : AppCompatActivity() {
    private val firstList = listOf(
        HorizontalButtonData(R.drawable.ic_outline_info_24, "1.14.5.14", "版本号"),
        HorizontalButtonData(R.drawable.ic_github, "Github", "点击扫码查看我们的开源项目"),
    )
    private val secondList = listOf(
        HorizontalButtonData(0, "480816699", "开发者 项目发起人"),
        HorizontalButtonData(0, "342729006", "设计师"),
    )
    private val thirdList = listOf(
        HorizontalButtonData(0, "198338518", "设计组织")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val recyclerView1 = findViewById<RecyclerView>(R.id.recyclerView1)
        val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerView2)
        val recyclerView3 = findViewById<RecyclerView>(R.id.recyclerView3)

        recyclerView1.layoutManager = LinearLayoutManager(this)
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView3.layoutManager = LinearLayoutManager(this)

        recyclerView1.adapter = HorizontalButtonAdapter(object : OnItemViewClickListener {
            override fun onClick(buttonName: String) {

            }
        }).also { it.submitList(firstList) }
        recyclerView2.adapter = UserHorizontalButtonAdapter(this).also { it.submitList(secondList) }
        recyclerView3.adapter = UserHorizontalButtonAdapter(this).also { it.submitList(thirdList) }
        findViewById<TextView>(R.id.pageName).setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }
}